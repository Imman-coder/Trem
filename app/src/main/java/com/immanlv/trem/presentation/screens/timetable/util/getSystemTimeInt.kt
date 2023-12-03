package com.immanlv.trem.presentation.screens.timetable.util

import java.text.SimpleDateFormat
import java.util.Date


/**
 * The function returns the current system time in integer format.
 *
 * @return An integer value representing the current system time in the format of hours and minutes in
 * 12-hour clock format.
 */
fun getSystemTimeInt(): Int {
    val sdf = SimpleDateFormat("hh:mma")
    val currentDate = sdf.format(Date())
    return timeToInt(currentDate)
}