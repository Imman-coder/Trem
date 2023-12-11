package com.immanlv.trem.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.BaseApplication
import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.use_case.AppPreferencesUseCases
import com.immanlv.trem.domain.use_case.LoginUseCases
import com.immanlv.trem.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val loginUseCases: LoginUseCases,
    private val profileUseCases: ProfileUseCases,
    private val appPreferencesUseCases: AppPreferencesUseCases,
    app: BaseApplication
) : ViewModel() {

    private val _state = mutableStateOf<MainViewState>(MainViewState.Idle)
    val state: State<MainViewState> = _state

    private val _isHomePrepareFinished = mutableStateOf(false)
    val isHomePrepareFinished: State<Boolean> = _isHomePrepareFinished

    private val _appPreference = mutableStateOf(AppPreference())
    val appPreference: State<AppPreference> = _appPreference

    val localLoginStatus: State<Boolean> = app.isLocalLoggedIn
    private var getAppPreferenceJob: Job? = null

    private val _hasInitialized = mutableStateOf(false)
    val hasInitialized: State<Boolean> = _hasInitialized

    init {
        getAppPreference()
        viewModelScope.launch {
            checkLoginStatus()
            delay(10)
            _hasInitialized.value = true
        }

    }

    fun onEvent(event: MainViewEvent){
        when(event){
            MainViewEvent.PrepareStartup -> prepareBeforeHomePage()
            MainViewEvent.RefreshProfile -> TODO()
            MainViewEvent.HandleLogout -> handleLogout()
        }
    }

    private fun prepareBeforeHomePage(){
        viewModelScope.launch {
            profileUseCases.refreshTimetable()
            _isHomePrepareFinished.value = true
        }
    }
    private fun handleLogout(){
        _isHomePrepareFinished.value = false
    }

    private suspend fun checkLoginStatus() {
        loginUseCases.updateLoginStatus()
    }

    private fun getAppPreference() {
        getAppPreferenceJob?.cancel()
        viewModelScope.launch {
            getAppPreferenceJob = appPreferencesUseCases.getAppPreference().onEach {
                _appPreference.value = it
            }.launchIn(viewModelScope)
        }
    }
}