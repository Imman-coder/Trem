package com.immanlv.trem.presentation.screens.timetable.util

import com.immanlv.trem.domain.model.Timetable


/**
 * The function returns the current event.
 *
 * @param time The input parameter "time" is an integer value representing the time
 * @return This function returns `Event` object representing the event that is currently happening at that time. */
fun Timetable.getCurrentEventTableIndexFromTime(time: Int): Int {
    var k = this.getCurrentTimeIndexFromTime(time)
    val event = -1
    var dayOfWeek = getSystemDayOfWeekInt()

    if (dayOfWeek > 6 || dayOfWeek < 0) dayOfWeek = 0

    if (k == 0) return 0

    for (x in 0 until this.EventTable[dayOfWeek].size) {
        val event1 = this.EventList[(this.EventTable[dayOfWeek][x] - 1)]
        if (k > event1.time_span) k -= event1.time_span
        else return x
    }
    return event
}