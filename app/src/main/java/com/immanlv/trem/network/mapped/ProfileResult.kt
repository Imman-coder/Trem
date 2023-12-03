package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.model.ProfileDto
import com.immanlv.trem.network.util.exception.LoginException


sealed class ProfileResult {
    data class Success(val data:ProfileDto): ProfileResult()
    data class Failed(val exception: Exception): ProfileResult()
}