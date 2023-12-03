package com.immanlv.trem.presentation.screens.login

sealed class LoginUiEvent {
    data class ShowToast(val message:String): LoginUiEvent()
    data class LogInProgress(val message: String): LoginUiEvent()
    data object LoggedIn: LoginUiEvent()
}
