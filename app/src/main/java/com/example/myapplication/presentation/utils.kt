package com.example.myapplication.presentation

import java.util.Locale

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

fun padZero(num: Int): String {
    return if (num < 10) {
        "0$num"
    } else {
        num.toString()
    }
}

fun  List<Int>.findNear(item:Int):Int{
    var t = 0
    for (x in 1  until  this.size){
        if(item > this[x])
            t=x
    }
    return t
}

fun timeToInt(timeString: String): Int {
    val timeArr = Regex("""^(\d{1,2}):(\d{2})(([ap]m)|([AP]M))${'$'}""").find(timeString)?.groupValues
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