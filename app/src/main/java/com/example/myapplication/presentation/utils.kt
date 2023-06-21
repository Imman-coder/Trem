package com.example.myapplication.presentation

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.app.ActivityCompat
import com.example.myapplication.domain.model.ClassType
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.Subject
import com.example.myapplication.domain.model.Timetable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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

/** This is an extension function for the `List<Int>` class. It takes an integer `item` as input and
returns the index of the element in the list that is closest to `item`. It does this by iterating
through the list and finding the element that is just smaller than `item`. If `item` is smaller than
the first element in the list, it returns 0. */
fun List<Int>.findNear(item: Int): Int {
    var t = 0
    for (x in 1 until this.size) {
        if (item > this[x])
            t = x
    }
    return t
}


/**
 * The function returns the current event.
 *
 * @param time The input parameter "time" is an integer value representing the time
 * @return This function returns `Event` object representing the event that is currently happening at that time. */
fun Timetable.getTodayEventFromTime(time: Int): Event {
    var k = this.TimeList.findNear(time)
    println(k)
    val event = Event(0, listOf(Subject("", "", "")), ClassType.Theory)
    var dayOfWeek = getSystemDayOfWeekInt()

    if (dayOfWeek > 6 || dayOfWeek < 0) dayOfWeek = 0

    if (k == 0) return this.EventList[this.EventTable[dayOfWeek][0] - 1]

    for (x in 0 until this.EventTable[dayOfWeek].size) {
        val event1 = this.EventList[this.EventTable[dayOfWeek][x] - 1]
        if (k > event1.time_span) k -= event1.time_span
        else return event1
    }
    return event
}

/**
 * The function converts a time string in the format "hh:mm AM/PM" to an integer representing the
 * number of minutes since midnight.
 *
 * @param timeString A string representing a time in the format "hh:mmam/pm" or "hh:mmAM/PM". For
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

/**
 * This function deletes the cache directory of a given context.
 *
 * @param context The context parameter is an object that provides access to application-specific
 * resources and classes, as well as information about the application's environment. It is typically
 * used to access system services, such as the cache directory in this example.
 */
fun deleteCache(context: Context) {
    try {
        val dir = context.cacheDir
        deleteDir(dir)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

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

/**
 * The function returns the integer value of the current day of the week (0-6, where 0 represents
 * Monday) using the Calendar class in Kotlin.
 *
 * @return The function `getSystemDayOfWeekInt()` returns an integer value representing the current day
 * of the week, where Monday is represented by 0, Tuesday by 1, Wednesday by 2, and so on.
 */
fun getSystemDayOfWeekInt(): Int {

    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.DAY_OF_WEEK) - 2
}

/**
 * This function deletes a directory and all its contents recursively.
 *
 * @param dir The "dir" parameter is a File object representing a directory that needs to be deleted.
 * @return a boolean value. It returns `true` if the directory or file was successfully deleted, and
 * `false` if it was not deleted.
 */
fun deleteDir(dir: File?): Boolean {
    return if (dir != null && dir.isDirectory) {
        val children = dir.list()
        for (i in children.indices) {
            val success = deleteDir(File(dir, children[i]))
            if (!success) {
                return false
            }
        }
        dir.delete()
    } else if (dir != null && dir.isFile) {
        dir.delete()
    } else {
        false
    }
}


/**
 * The function checks if the device is currently connected to the internet via cellular, wifi, or
 * ethernet network.
 *
 * @param context The context parameter is a reference to the current state of the application or
 * activity. It provides access to resources, preferences, and other system-level services.
 * @return A boolean value indicating whether the device is currently connected to the internet or not.
 */
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
    }
    return false
}

fun getTodayLastTime(timetable: Timetable):Int{

    val todayDay = getSystemDayOfWeekInt()
    val todayEvents = timetable.EventTable[todayDay]
    var totalBlock = 0

    for (event in todayEvents){
        totalBlock += timetable.EventList[event-1].time_span
    }
    return timetable.TimeList[totalBlock]

}
fun generateLogTag(className: String): String {
    val maxTagLength = 23 // Maximum length allowed for a log tag
    val tagPrefix = "MyApp" // Your desired prefix for the log tag

    // Remove any unwanted characters from the class name
    val cleanClassName = className.replace("[^a-zA-Z0-9_]".toRegex(), "")

    // Combine the prefix and cleaned class name
    val tag = "$tagPrefix-${cleanClassName.substring(0, minOf(cleanClassName.length, maxTagLength - tagPrefix.length))}"

    return tag
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoScrollEffect(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        content()
    }
}