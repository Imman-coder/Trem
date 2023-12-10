package com.immanlv.trem.presentation.screens.timetable.util

import com.immanlv.trem.domain.model.Timetable


fun getTodayLastTime(timetable: Timetable):Int{

    val todayDay = getSystemDayOfWeekInt()
    val todayEvents = timetable.eventTable[todayDay]
    var totalBlock = 0

    for (event in todayEvents){
        totalBlock += timetable.eventList[(event-1)].timeSpan
    }
    return timetable.timeList[totalBlock]

}