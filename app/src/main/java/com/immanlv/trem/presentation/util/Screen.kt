package com.immanlv.trem.presentation.util

sealed class Screen(
    val route:String
) {
    data object SettingsMain: Screen("settings_main")
    data object SettingsNotification: Screen("settings_notification")

    data object Login: Screen("login")

    data object Timetable: Screen("timetable")
    data object Home: Screen("home")
    data object Profile: Screen("profile")
    data object Attendance: Screen("scorecard")
    data object TimetableBuilder: Screen("timetable_builder")


    data object AuthNavGraph: Screen("auth")
    data object MainNavGraph: Screen("main")
    data object SettingsNavGraph: Screen("settings")
}