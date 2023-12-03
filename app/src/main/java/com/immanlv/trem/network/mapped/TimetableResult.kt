package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.model.AttendanceDto
import com.immanlv.trem.network.model.TimetableDto
import com.immanlv.trem.network.util.exception.LoginException


sealed class TimetableResult {
    data class Success(val data:TimetableDto): TimetableResult()
    data class Failed(val exception: LoginException): TimetableResult()
}