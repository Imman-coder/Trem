package com.immanlv.trem.presentation

sealed class MainViewEvent {
    data object RefreshProfile: MainViewEvent()
    data object PrepareStartup: MainViewEvent()
    data object HandleLogout: MainViewEvent()
}