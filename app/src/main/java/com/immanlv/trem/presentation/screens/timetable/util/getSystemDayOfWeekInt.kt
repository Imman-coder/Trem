package com.immanlv.trem.presentation.screens.timetable.util

import java.util.Calendar


/**
 * The function returns the integer value of the current day of the week (0-6, where 0 represents
 * Monday) using the Calendar class in Kotlin.
 *
 * @return The function `getSystemDayOfWeekInt()` returns an integer value representing the current day
 * of the week, where Monday is represented by 0, Tuesday by 1, Wednesday by 2, and so on.
 * Sunday       -> -1
 * Monday       ->  0
 * Tuesday      ->  1
 * Wednesday    ->  2
 * Thursday     ->  3
 * Friday       ->  4
 * Saturday     ->  5
 */
fun getSystemDayOfWeekInt(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.DAY_OF_WEEK) - 2
}