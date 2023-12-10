package com.immanlv.trem.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.BaseApplication
import com.immanlv.trem.data.model.Credentials
import com.immanlv.trem.data.model.LoginStatusE
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.use_case.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val app: BaseApplication
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<LoginUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    lateinit var credential: Credentials

    init {
        viewModelScope.launch {
            credential = loginUseCases.getCredentials()
        }

    }


    private var _loginStateObserver: Job? = null

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event.username, event.password, event.saveCredentials)

            is LoginEvent.FakeLogin -> fakeLogin(
                event.sem,
                event.section,
                event.branch,
                event.batch,
                event.program
            )
        }

    }

    private fun fakeLogin(sem: Int, section: Char, branch: String, batch: String, program: String) {
        viewModelScope.launch {
            loginUseCases.fakeLogin(
                Profile(
                    sem = sem,
                    section = section,
                    branch = branch,
                    batch = batch,
                    program = program,
                )
            )
            _loginStateObserver?.cancel()
        }
    }

    private fun login(username: String, password: String, saveCredentials: Boolean) {
        viewModelScope.launch {
            _eventFlow.emit(LoginUiEvent.LogInProgress("Starting up... "))

            val state = loginUseCases.login(username, password, saveCredentials)
            _loginStateObserver?.cancel()
            _loginStateObserver = state.onEach {
                when (it) {
                    LoginStatusE.FetchingHash -> {
                        _eventFlow.emit(LoginUiEvent.LogInProgress("Fetching Hash..."))
                    }

                    LoginStatusE.LoggingIn -> {
                        _eventFlow.emit(LoginUiEvent.LogInProgress("Logging In..."))
                    }

                    LoginStatusE.CheckingLoginStatus -> {
                        _eventFlow.emit(LoginUiEvent.LogInProgress("Checking Login Status..."))
                    }

                    LoginStatusE.BindingUp -> {
                        _eventFlow.emit(LoginUiEvent.LogInProgress("Finishing..."))
                    }

                    LoginStatusE.Success -> {
                        _eventFlow.emit(LoginUiEvent.LoggedIn)
                        _loginStateObserver?.cancel()
                    }

                    is LoginStatusE.Error -> {
                        _eventFlow.emit(LoginUiEvent.ShowToast(it.error.message.toString()))
                        _loginStateObserver?.cancel()
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

}

