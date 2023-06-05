package com.example.myapplication.domain.model

import android.content.res.Resources.Theme
import kotlinx.serialization.Serializable

@Serializable
data class AppPreferences(
    val showNotifications:Boolean,
    val showDownloadOption:Boolean,
    val swipeToTimetable:Boolean,
//    val theme: Theme?=null
)
