package com.immanlv.trem.presentation.screens.timetable

sealed class TimetableState {
    data object Idle:TimetableState()
    sealed class Loading {
        data object Fetching:TimetableState()
        data object Retrieving:TimetableState()
    }
    data class Error(val error: Exception) : TimetableState()
}