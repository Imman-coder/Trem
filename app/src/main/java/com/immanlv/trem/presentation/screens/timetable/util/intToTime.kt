package com.immanlv.trem.presentation.screens.timetable.util


/**
 * The function converts an integer representing minutes into a string representing time in 12-hour
 * format.
 *
 * @param minutes The input parameter "minutes" is an integer value representing the total number of
 * minutes.
 * @return The function `intToTime` takes an integer value representing minutes and returns a string
 * representing the time in 12-hour format. The returned string includes the hours and minutes
 * separated by a colon, and the time of day (AM or PM) based on the value of hours. The function uses
 * the `padZero` function to ensure that the minutes are always represented with two digits.
 */
fun intToTime(minutes: Int): String {
    val hours = minutes / 60
    val minutesRemaining = minutes % 60

    return when {
        hours == 0 -> "12:${padZero(minutesRemaining)}AM"
        hours < 12 -> "$hours:${padZero(minutesRemaining)}AM"
        hours == 12 -> "12:${padZero(minutesRemaining)}PM"
        else -> "${hours - 12}:${padZero(minutesRemaining)}PM"
    }
}


/**
 * The function pads a single digit number with a leading zero.
 *
 * @param num num is an integer parameter representing the number that needs to be padded with a zero
 * if it is less than 10.
 * @return The function `padZero` takes an integer `num` as input and returns a string. If `num` is
 * less than 10, the function returns a string with a leading zero followed by the value of `num`. If
 * `num` is greater than or equal to 10, the function returns a string representation of `num`.
 */
fun padZero(num: Int): String {
    return if (num < 10) {
        "0$num"
    } else {
        num.toString()
    }
}