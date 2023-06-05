package com.example.myapplication.network.exceptions

import com.example.myapplication.presentation.navigation.login.LoginViewModel
import java.lang.Error

class LoginException(message: String, val toastMessage: String, val error: Error) : Throwable(message = message) {
    sealed class Error{
        object InvalidCredentials : Error()
        object NetworkError : Error()
        object FetchFailed : Error()
        object AlreadyLoggedIn : Error()
    }
}