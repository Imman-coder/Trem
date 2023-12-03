package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.util.exception.LoginException


sealed class SicResult {
    data class Success(val data:String): SicResult()
    data class Failed(val exception: LoginException): SicResult()
}