package com.example.myapplication.presentation.components.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.presentation.MainActivity2
import java.text.SimpleDateFormat
import java.util.Date


class TimetableNotificationService(private val context: Context) {
    private val TAG = "TimetableNotificationService"

    private val stopNotificationIntent = PendingIntent.getBroadcast(
        context,
        1,
        Intent(context, StopNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )!!

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val updateTimetableIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, UpdateTimetableReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private fun registerAManager() {

        Log.d(TAG, "registerAManager: Registered")
        val sdf = SimpleDateFormat("ss")
        val currentDate = sdf.format(Date())

        val atInterval = 1000 * (60-currentDate.toInt())

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis()+atInterval,
            updateTimetableIntent
        )
    }

    fun unregisterAManager() {
        Log.d(TAG, "unregisterAManager: Unregistered")
        alarmManager.cancel(updateTimetableIntent)
        updateTimetableIntent.cancel()
        hideNotification()
        registered = false
    }


    fun showNotification(data: NotificationWrapper) {

        val actionIntent = Intent(context, MainActivity2::class.java)
        actionIntent.putExtra(TIMETABLE_NOTIFICATION_RECEIVER, true)

        val timetablePendingIntent = PendingIntent.getActivity(
            context,
            0,
            actionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        var notificationBuilder = NotificationCompat.Builder(context, TIMETABLE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(data.title)
            .setContentText(data.content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setOnlyAlertOnce(true)
//            .setContentIntent(timetablePendingIntent)

        if (data.eventType != EventType.Finished)
            notificationBuilder = notificationBuilder
                .setOngoing(true)
                .addAction(
                    R.drawable.baseline_visibility_24, "Stop", stopNotificationIntent
                )


        if (data.eventType == EventType.Finished) {
            if (registered)
                unregisterAManager()
        } else if (!registered) {
            registerAManager()
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(TIMETABLE_ID, notification)
    }


    private fun hideNotification() {
        notificationManager.cancel(TIMETABLE_ID)
    }


    companion object {
        const val TIMETABLE_CHANNEL_ID = "timetable_channel"
        const val TIMETABLE_NOTIFICATION_RECEIVER = "byNotification"
        private var registered = false
        private const val TIMETABLE_ID = 101

    }
}


data class NotificationWrapper(
    var title: String = "",
    var content: String = "",
    var notifyNotificationType: NotificationType= NotificationType.Cancelable,
    var eventType: EventType = EventType.Starting,
)

enum class NotificationType{
    Permanent, Cancelable,
}

enum class EventType{
    Starting, Ongoing, Finished
}