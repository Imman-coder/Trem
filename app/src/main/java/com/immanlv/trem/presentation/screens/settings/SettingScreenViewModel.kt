package com.immanlv.trem.presentation.screens.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.di.util.CookieServer
import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.use_case.AppPreferencesUseCases
import com.immanlv.trem.domain.use_case.LoginUseCases
import com.immanlv.trem.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingScreenViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val loginUseCases: LoginUseCases,
    private val appPreferencesUseCases: AppPreferencesUseCases
) :
    ViewModel() {


    private val _profile = mutableStateOf(Profile())
    val profile: State<Profile> = _profile
    private val _appPreference = mutableStateOf(AppPreference())
    val appPreference: State<AppPreference> = _appPreference

    private var getProfileJob: Job? = null
    private var getAppPreferenceJob: Job? = null

    init {
        getProfile()
        getAppPreference()
    }

    fun onEvent(event: SettingScreenEvent) {
        when (event) {
            is SettingScreenEvent.Logout -> logout()
            is SettingScreenEvent.SetPreference -> setPreference(event.v)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            loginUseCases.logout()
        }
    }

    private fun setPreference(pref: AppPreference) {
        viewModelScope.launch {
            appPreferencesUseCases.setAppPreference(pref)
        }
    }


    private fun getProfile() {
        getProfileJob?.cancel()
        viewModelScope.launch {
            getProfileJob = profileUseCases.getProfile().onEach {
                _profile.value = it
            }.launchIn(viewModelScope)
        }
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