package com.example.myapplication.presentation.components.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.core.DataStore
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Timetable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class UpdateTimetableReceiver:BroadcastReceiver() {

    @Inject
    lateinit var timetable: DataStore<Timetable>

    private val TAG = "UpdateTimetableBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch{
            Log.d(TAG, "onReceive: Updated timetable notification")
            val sdf = SimpleDateFormat("hh:mm:ss")
            val currentDate = sdf.format(Date())

            Log.d(TAG, "onReceive: at time $currentDate")

            val timetable = timetable.data.first()

            val w = NotificationContentBuilder.buildContent(timetable)

            TimetableNotificationService(context).showNotification(w)

        }
    }
}