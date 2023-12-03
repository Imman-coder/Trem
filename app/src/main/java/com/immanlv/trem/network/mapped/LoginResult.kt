package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.util.exception.LoginException


sealed class LoginResult {
    data class Success(val data: Boolean): LoginResult()
    data class Failed(val exception: LoginException): LoginResult()
}