package com.immanlv.trem.data.model


sealed class LoginStatusE{
    data object FetchingHash:LoginStatusE()
    data object LoggingIn:LoginStatusE()
    data object CheckingLoginStatus:LoginStatusE()
    data object BindingUp:LoginStatusE()
    data object Success:LoginStatusE()
    data class Error(val error:Exception):LoginStatusE()
}
