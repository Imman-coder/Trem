package com.immanlv.trem.presentation.screens.timetableBuilder.util

import java.util.Locale


fun simplifyTeacherName(name: String): String {
    var acr = ""
    if (name.indexOf("(") >= 0)
        acr = name.substring(name.indexOf("(") + 1, name.indexOf(")"))
    if (acr != "") return acr
    if (name.length < 5) return name
    val words = name.split(" ")
    for (i in words.indices) {
        if(words[i].isEmpty()) continue
        if (!words[i].lowercase(Locale.ROOT).contains("prof")) acr += words[i][0] ?: ""
    }
    return acr
}