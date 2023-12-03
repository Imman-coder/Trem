package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.model.AttendanceDto


sealed class AttendanceResult {
    data class Success(val data:AttendanceDto): AttendanceResult()
    data class Failed(val exception: Exception): AttendanceResult()
}