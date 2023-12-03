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
):ViewModel(){

    private val _eventFlow = MutableSharedFlow<LoginUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    lateinit var credential :Credentials

    init {
        viewModelScope.launch {
            credential = loginUseCases.getCredentials()
        }

    }


    private var _loginStateObserver: Job? = null

    fun onEvent(event: LoginEvent){
        viewModelScope.launch {
            when(event){
                is LoginEvent.Login->{
                    val state = loginUseCases.login(event.username,event.password,event.saveCredentials)
                    _loginStateObserver?.cancel()
                    _loginStateObserver = state.onEach {
                        when(it){
                            LoginStatusE.FetchingHash ->{
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

                is LoginEvent.FakeLogin ->{
                    val state = loginUseCases.fakeLogin(Profile(
                        sem = event.sem,
                        section = event.section,
                        branch = event.branch,
                        batch = event.batch,
                        program = event.program,
                    ))
                    _loginStateObserver?.cancel()
//                    _loginStateObserver = state.onEach {
//                        when(it){
//                            is LoginState.Loading -> {
//                                _eventFlow.emit(LoginUiEvent.LogInProgress(""))
//                            }
//                            is LoginState.Success -> {
//                                _eventFlow.emit(LoginUiEvent.LoggedIn)
//                                _loginStateObserver?.cancel()
//                            }
//                            is LoginState.Error -> {
//                                _eventFlow.emit(LoginUiEvent.ShowToast(it.error.message.toString()))
//                                _loginStateObserver?.cancel()
//                            }
//                        }
//                    }.launchIn(viewModelScope)
                }
            }
        }
    }

}

