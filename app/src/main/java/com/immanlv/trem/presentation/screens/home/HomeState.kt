package com.immanlv.trem.presentation.screens.home

sealed class HomeState {
    data object Idle:HomeState()
    sealed class Loading {
        data object Fetching:HomeState()
        data object Retrieving:HomeState()
    }
    data class Error(val error: Exception) : HomeState()
}