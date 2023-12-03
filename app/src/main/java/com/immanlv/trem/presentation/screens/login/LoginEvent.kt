package com.immanlv.trem.presentation.screens.login

sealed class LoginEvent {
    data class Login(val username: String, val password: String, val saveCredentials: Boolean) :
        LoginEvent()

    data class FakeLogin(
        val sem: Int,
        val section: Char,
        val branch: String,
        val batch: String,
        val program: String
    ) : LoginEvent()
}
