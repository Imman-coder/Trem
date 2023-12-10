package com.immanlv.trem.presentation.screens.attendance

sealed class AttendanceUiState {
    data object Idle:AttendanceUiState()
    sealed class Loading {
        data object Fetching:AttendanceUiState()
        data object Retrieving:AttendanceUiState()
    }
    data class Error(val error: Exception) : AttendanceUiState()
}