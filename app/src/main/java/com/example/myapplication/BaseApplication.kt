package com.example.myapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.presentation.components.notification.TimetableNotificationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {

    var isLoggedIn by mutableStateOf(false)



    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {

        val notificationChannel = NotificationChannel(
            TimetableNotificationService.TIMETABLE_CHANNEL_ID,"Timetable",
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.description = "Used to show class time status"

        val notificationManager =   getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

}