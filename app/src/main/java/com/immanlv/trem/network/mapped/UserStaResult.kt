package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.util.exception.LoginException


sealed class UserStaResult {
    data class Success(val data:String): UserStaResult()
    data object Failed: UserStaResult()
}