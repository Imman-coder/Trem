package com.immanlv.trem.presentation.screens.timetable.util


/**
 * The function converts a time string in the format "hh:mm AM/PM" to an integer representing the
 * number of minutes since midnight.
 *
 * @param timeString A string representing a time in the format "hh:mm am/pm" or "hh:mmAM/PM". For
 * example, "09:30am" or "07:45PM".
 * @return an integer value representing the total number of minutes elapsed since midnight for the
 * given time string in the format "hh:mm AM/PM".
 */
fun timeToInt(timeString: String): Int {
    val timeArr =
        Regex("""^(\d{1,2}):(\d{2})(([ap]m)|([AP]M))${'$'}""").find(timeString)?.groupValues
            ?: throw IllegalArgumentException("Invalid time format")

    val hours = timeArr[1].toInt()
    val minutes = timeArr[2].toInt()
    val meridiem = timeArr[3].lowercase()

    val convertedHours = when {
        meridiem == "pm" && hours != 12 -> hours + 12
        meridiem == "am" && hours == 12 -> 0
        meridiem == "PM" && hours != 12 -> hours + 12
        meridiem == "AM" && hours == 12 -> 0
        else -> hours
    }

    return convertedHours * 60 + minutes
}