package com.immanlv.trem.presentation.screens.profile

sealed class ProfileScreenEvent{
    data object RefreshScorecard:ProfileScreenEvent()
    data object RefreshProfile:ProfileScreenEvent()
}
