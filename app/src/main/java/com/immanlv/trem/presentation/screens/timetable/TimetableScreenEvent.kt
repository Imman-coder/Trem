package com.immanlv.trem.presentation.screens.timetable

sealed class TimetableScreenEvent {
    data object RefreshTimetable : TimetableScreenEvent()
    data class ShowClassCard(val subCode:String):TimetableScreenEvent()
    data object HideClassCard : TimetableScreenEvent()
}