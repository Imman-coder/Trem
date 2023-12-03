package com.immanlv.trem.presentation

sealed class MainViewEvent {
    data object RefreshProfile: MainViewEvent()
}