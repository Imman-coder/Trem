package com.example.myapplication.presentation.navigation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BaseApplication
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val credentialsDataStore: DataStore<Credentials>,
    private val profileDataStore: DataStore<Profile>,
    private val profileRepository: ProfileRepository,
    private val app: BaseApplication

    ) : ViewModel() {

    private val _credentials = MutableStateFlow<Credentials?>(null)
    val credentials: StateFlow<Credentials?> = _credentials

    private val _loginUiState = mutableStateOf(LoginUiState())
    val uiState: State<LoginUiState> = _loginUiState

    init {
        loadCredentials()
    }

    private fun loadCredentials() {
        viewModelScope.launch {
            credentialsDataStore.data.collect { userData ->
                _credentials.value = userData
                println(userData)
                if (userData.isFakeLoggedIn)
                    _loginUiState.value = LoginUiState(loggedInAs = LoginUiState.LoggedInUser.Fake)
                else if (userData.hasCredentials)
                    _loginUiState.value =
                        LoginUiState(loggedInAs = LoginUiState.LoggedInUser.Original)
                else
                    _loginUiState.value = LoginUiState(loggedInAs = LoginUiState.LoggedInUser.Not)
            }
        }
    }

    fun setupFakeUser(sem: Int, program: String, branch: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState(loggedInAs = LoginUiState.LoggedInUser.Not ,isLogging = true)
            credentialsDataStore.updateData { Credentials(isFakeLoggedIn = true) }
            profileDataStore.updateData { Profile(sem = sem, program = program, branch = branch) }
            _loginUiState.value =
                LoginUiState(isLogging = false, loggedInAs = LoginUiState.LoggedInUser.Fake)

        }
    }

    fun login(username: String, password: String, save: Boolean) {

        viewModelScope.launch {

            _loginUiState.value = LoginUiState(isLogging = true,loggedInAs = LoginUiState.LoggedInUser.Not)
            try {
                val p = profileRepository.Login(username, password)
                profileDataStore.updateData { p }
                app.isLoggedIn = true
                if (save) saveCredentials(username, password)
                _loginUiState.value = LoginUiState(loggedInAs = LoginUiState.LoggedInUser.Original)
            } catch (e: LoginException) {
                e.printStackTrace()
                _loginUiState.value =
                    LoginUiState(loggedInAs = LoginUiState.LoggedInUser.Not ,error = if (e.error == LoginException.Error.InvalidCredentials) LoginUiState.Error.InvalidCredentials else LoginUiState.Error.NetworkError)
            }

        }

    }


    private suspend fun saveCredentials(username: String, password: String) {
        credentialsDataStore.updateData { currentData ->
            currentData.copy(
                hasCredentials = true,
                uid = username,
                pass = password,
                isFakeLoggedIn = false
            )
        }
    }

    data class LoginUiState(
        val isLogging: Boolean = false,
        val error: Error? = null,
        val loggedInAs: LoggedInUser? = null,
    ) {
        sealed class Error {
            object NetworkError : Error()
            object InvalidCredentials : Error()
        }

        enum class LoggedInUser {
            Original, Fake, Not
        }
    }
}
