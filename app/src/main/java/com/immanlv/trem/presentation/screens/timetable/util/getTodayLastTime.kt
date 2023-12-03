package com.immanlv.trem.presentation.screens.timetable.util

import com.immanlv.trem.domain.model.Timetable


fun getTodayLastTime(timetable: Timetable):Int{

    val todayDay = getSystemDayOfWeekInt()
    val todayEvents = timetable.EventTable[todayDay]
    var totalBlock = 0

    for (event in todayEvents){
        totalBlock += timetable.EventList[(event-1)].time_span
    }
    return timetable.TimeList[totalBlock]

}