package com.immanlv.trem.presentation.screens.attendance

sealed class AttendanceViewEvent {
    data object GetAttendanceView:AttendanceViewEvent()
    data object RefreshAttendanceView:AttendanceViewEvent()
    data class RefreshLateUpdated(val code: String) : AttendanceViewEvent()
}