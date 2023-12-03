package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.util.exception.LoginException


sealed class HashResult {
    data class Success(val data:String): HashResult()
    data class Failed(val exception: LoginException): HashResult()
}