package com.example.myapplication.network.exceptions

class FetchException(message:String,toastMessage:String): Throwable(message = message) {
    val toastMessage = toastMessage
}