package com.immanlv.trem.presentation

import com.immanlv.trem.domain.model.AttendanceSubject

sealed class MainViewState {
    data class Error(val message:String): MainViewState()
    data class ShowAttendanceDetailModal(val data:AttendanceSubject): MainViewState()
    data object Idle: MainViewState()
}