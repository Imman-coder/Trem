package com.immanlv.trem.network.util.exception

class LoginException(msg:String,val type:Type):Exception(msg) {
    sealed class Type{
        object AlreadyLoggedIn:Type()
        object FailedToGetHash:Type()
        object InvalidCredentials:Type()
        object NoCredentials:Type()
    }
}