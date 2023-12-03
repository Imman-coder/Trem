package com.immanlv.trem.presentation.screens.profile


sealed class ProfileState {
    data object Idle:ProfileState()
    sealed class Loading {
        data object Fetching:ProfileState()
        data object Retrieving:ProfileState()
    }
    data class Error(val error: Exception) : ProfileState()
}

sealed class ScorecardState {
    data object Idle:ProfileState()
    sealed class Loading {
        data object Fetching:ProfileState()
        data object Retrieving:ProfileState()
    }
    data class Error(val error: Exception) : ProfileState()
}