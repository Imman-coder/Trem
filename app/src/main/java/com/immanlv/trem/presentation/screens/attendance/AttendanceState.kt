package com.immanlv.trem.presentation.screens.attendance

sealed class AttendanceState {
    data object Idle:AttendanceState()
    sealed class Loading {
        data object Fetching:AttendanceState()
        data object Retrieving:AttendanceState()
    }
    data class Error(val error: Exception) : AttendanceState()
}