package com.immanlv.trem.presentation.screens.timetable.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.immanlv.trem.domain.model.Timetable
import java.io.File

/** This is an extension function for the `List<Int>` class. It takes an integer `item` as input and
returns the index of the element in the list that is closest to `item`. It does this by iterating
through the list and finding the element that is just smaller than `item`. If `item` is smaller than
the first element in the list, it returns 0. */
fun Timetable.getCurrentTimeIndexFromTime(time: Int): Int {
    val timeList = this.TimeList
    var t = 0
    for (x in 1 until timeList.size) {
        if (time > timeList[x])
            t = x
    }
    return t
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

fun generateLogTag(className: String): String {
    val maxTagLength = 23 // Maximum length allowed for a log tag
    val tagPrefix = "MyApp" // Your desired prefix for the log tag

    // Remove any unwanted characters from the class name
    val cleanClassName = className.replace("[^a-zA-Z0-9_]".toRegex(), "")

    // Combine the prefix and cleaned class name
    val tag = "$tagPrefix-${cleanClassName.substring(0, minOf(cleanClassName.length, maxTagLength - tagPrefix.length))}"

    return tag
}
