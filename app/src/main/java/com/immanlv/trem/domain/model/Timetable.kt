package com.immanlv.trem.domain.model

import com.immanlv.trem.domain.model.util.filter
import com.immanlv.trem.domain.util.DataErrorType
import com.immanlv.trem.presentation.screens.timetable.util.getSystemDayOfWeekInt
import com.immanlv.trem.presentation.screens.timetable.util.getSystemTimeInt
import com.immanlv.trem.presentation.screens.timetable.util.intToTime
import kotlinx.serialization.Serializable
import java.util.Calendar.MONDAY
import java.util.Calendar.SATURDAY


@Serializable
data class Timetable(
    val eventTable: List<List<Int>> = listOf(
        listOf(),
        listOf(),
        listOf(),
        listOf(),
        listOf(),
        listOf(),
    ),
    val timeList: List<Int> = listOf(),
    val eventList: List<Event> = listOf(),
    val error: DataErrorType = DataErrorType.NoError
) {
    val todayClasses: List<Event>
        get() = todayEvents()
    val pastClasses: List<Event>
        get() = pastEvents()
    val ongoingClass: Event?
        get() = ongoingEvent()
    val upcomingClasses: List<Event>
        get() = upcomingEvents()


    val timeStamps: List<String>
        get() = timeList.map { intToTime(it) }


    val upcomingClassCount: Pair<Int, Int>
        get() = upcomingClasses.let {
            Pair(
                it.filter(listOf(ClassType.Theory)).size,
                it.filter(listOf(ClassType.Lab)).size
            )
        }


    val hasClassesToday: Boolean
        get() = getSystemDayOfWeekInt() in 0..5
    val classStarted: Boolean
        get() = timeList[0] <= getSystemTimeInt()
    val classFinished: Boolean
        get() = timeList[timeList.size - 1] < getSystemTimeInt()


    fun getAt(index: Int): List<Event> = if (index in MONDAY..SATURDAY)
        eventTable[index - 2].map { eventList[it - 1] } else listOf()


    private fun getOngoingEventIndexOnTable(): Int {
        val currentTime = getSystemTimeInt()
        var currentEventIndex = 0

        timeList.forEachIndexed { index, i ->
            if (currentTime > i) {
                currentEventIndex = index
                return@forEachIndexed
            }
        }

        todayEvents().forEachIndexed { index, it ->
            currentEventIndex -= it.timeSpan
            if (currentEventIndex < 0) {
                return index
            }
        }
        return -1
    }


    private fun todayEvents(): List<Event> {
        val currentDay = getSystemDayOfWeekInt()
        if (eventTable.isEmptyLists() || !hasClassesToday)
            return listOf()
        return eventTable[currentDay].map { eventList[it - 1] }
    }


    private fun pastEvents(): List<Event> {
        val todayEvents = todayEvents()
        return getOngoingEventIndexOnTable().let {
            if (it >= 0) todayEvents.subList(0, it) else listOf()
        }
    }


    private fun ongoingEvent(): Event? {
        val currentTime = getSystemTimeInt()

        if (timeList.isEmpty() || currentTime !in timeList.first()..timeList.last())
            return null

        val todayEvents = todayEvents()

        return getOngoingEventIndexOnTable().let { if (it >= 0) todayEvents[it] else null }
    }

    private fun upcomingEvents(): List<Event> {
        val todayEvents = todayEvents()
        return getOngoingEventIndexOnTable().let {
            if (it >= 0) todayEvents.subList(it + 1, todayEvents.size - 1) else listOf()
        }
    }

    private fun List<List<Int>>.isEmptyLists(): Boolean {
        if (this.isEmpty()) return true
        this.forEach {
            if (it.isEmpty()) return true
        }
        return false
    }

    fun getSummaryOfClass(code: String): TimetableStat? {
        var eventId = 0
        var weeklyClasses = 0
        val classesOnDay = mutableListOf<Int>()


        eventList.forEachIndexed { index, it ->
            try {
                if (it.subjects[0].subjectCode == code) eventId = index + 1
            } catch (_:Exception) {
                return null
            }

        }

        eventTable.forEachIndexed { id, it ->
            for (i in it) {
                if (i == eventId) {
                    weeklyClasses++
                    if (id !in classesOnDay)
                        classesOnDay += id
                }
            }
        }

        return TimetableStat(
            weeklyClasses = weeklyClasses,
            classesInWeek = classesOnDay
        )
    }
}

@Serializable
data class Event(
    var timeSpan: Int,
    val subjects: List<Subject>,
    val classType: ClassType
)

@Serializable
data class Subject(
    val subject: String,
    val subjectCode: String,
    val teacher: String
)

enum class ClassType {
    Theory, Lab, Notice
}


data class TimetableStat(
    val weeklyClasses: Int,
    val classesInWeek: List<Int>
)