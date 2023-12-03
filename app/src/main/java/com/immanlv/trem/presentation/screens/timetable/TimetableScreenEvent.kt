package com.immanlv.trem.presentation.screens.timetable

sealed class TimetableScreenEvent {
    data object RefreshTimetable : TimetableScreenEvent()
}