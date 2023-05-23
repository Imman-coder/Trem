package com.example.myapplication.presentation

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