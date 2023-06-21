package com.example.myapplication.network.exceptions


class FetchException(message:String, val toastMessage:String,val error: Error): Throwable(message = message) {

    sealed class Error{
        object NetworkError : Error()
        object NoInternet : Error()
        object NotLoggedIn : Error()
    }

}