package com.immanlv.trem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppPreference(
    val showNotification:Boolean = false,
    val colorMode: ColorMode = ColorMode.Unspecified,
    val disableCaching: Boolean = false,
    val updateNotificationOnAppClose: Boolean = false,
    val developerMode: Boolean = false,
    val loadCustomTimetable:String = "",
    val preClassTemplate: String = "",
    val postClassTemplate: String = "",
    val ongoingClassTemplate: String = "",
    val autoFetchLastUpdated: Boolean = false,
)

enum class ColorMode {
    Dark,Light,Unspecified
}