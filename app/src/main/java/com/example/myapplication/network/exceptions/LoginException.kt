package com.example.myapplication.network.exceptions

class LoginException(message: String, toastMessage: String) : Throwable(message = message) {
    val toastMessage = toastMessage;
}