package com.example.myapplication.presentation.components.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopNotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val service = TimetableNotificationService(context)
        service.unregisterAManager()
    }
}