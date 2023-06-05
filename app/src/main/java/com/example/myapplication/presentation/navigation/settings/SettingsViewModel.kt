package com.example.myapplication.presentation.navigation.settings

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.AppPreferences
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject constructor(
        private val appPreferences: DataStore<AppPreferences>,
        private val credentialStore: DataStore<Credentials>,
        private val profileStore: DataStore<Profile>,
    ): ViewModel() {


    suspend fun setNotificationSetting(value:Boolean){
        appPreferences.updateData { it.copy(
            showNotifications = value
        ) }
    }

    suspend fun setDownloadSetting(value:Boolean){
        appPreferences.updateData { it.copy(
            showDownloadOption = value
        ) }
    }

    fun getAppPreference() = appPreferences



    suspend fun logOut() {
        credentialStore.updateData { Credentials() }
        profileStore.updateData { Profile() }
    }
}