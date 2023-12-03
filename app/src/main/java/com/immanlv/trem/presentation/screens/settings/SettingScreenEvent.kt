package com.immanlv.trem.presentation.screens.settings

import com.immanlv.trem.domain.model.AppPreference

sealed class SettingScreenEvent{
    data object Logout:SettingScreenEvent()
    data class SetPreference(val v: AppPreference) : SettingScreenEvent()
}
