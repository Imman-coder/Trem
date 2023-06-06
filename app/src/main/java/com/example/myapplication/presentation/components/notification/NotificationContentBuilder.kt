package com.example.myapplication.presentation.components.notification

import com.example.myapplication.domain.model.ClassType
import com.example.myapplication.domain.model.Timetable
import com.example.myapplication.presentation.findNear
import com.example.myapplication.presentation.getSystemDayOfWeekInt
import com.example.myapplication.presentation.getSystemTimeInt
import com.example.myapplication.presentation.getTodayEventFromTime
import com.example.myapplication.presentation.getTodayLastTime
import com.example.myapplication.presentation.intToTime


class NotificationContentBuilder {

    companion object {

        private const val TAG = "NotificationContentBuilder"

        fun buildContent(timetable: Timetable): NotificationWrapper {
            val currentTime = getSystemTimeInt()
            var currentDay = getSystemDayOfWeekInt()
            if(currentDay>6 || currentDay<0) currentDay = 0
            val message = NotificationWrapper()


            if (currentTime < timetable.TimeList[0]) {
                val event =
                    timetable.EventList[timetable.EventTable[currentDay][0]-1]
                message.title = "Upcoming class"
                val timeLeft = timetable.TimeList[0] - currentTime

                message.content =
                    if (timeLeft == 1) "${event.subjects[0].subject} in a minute"
                    else "${event.subjects[0].subject} in ${timetable.TimeList[0] - currentTime} minutes"
                message.notifyNotificationType = NotificationType.Permanent

            }


            else if (currentTime > getTodayLastTime(timetable)) {
                message.title = "Class Finished!"
                message.content = "All class finished for today!"
                message.eventType = EventType.Finished
            }

            else{
                val event = timetable.getTodayEventFromTime(currentTime)
                message.title = "Ongoing class"
                val kop = timetable.TimeList.findNear(currentTime)
                message.content =
                    "${event.subjects[0].subject} -> ${intToTime(timetable.TimeList[kop])} - ${
                        intToTime(timetable.TimeList[kop + event.time_span])
                    }"

                if (event.class_type == ClassType.Notice) {
                    message.title = event.subjects[0].subject
                    message.content =
                        "${timetable.TimeList[0] - currentTime} minutes remaining"
                }

                if (event.class_type == ClassType.Lab) {
                    message.title = "Ongoing Lab"
                    message.content =
                        "${event.subjects[0].subject} -> ${intToTime(timetable.TimeList[kop])} - ${
                            intToTime(timetable.TimeList[kop + event.time_span])
                        }"
                }
                message.eventType = EventType.Ongoing
            }
            return message
        }

    }
}