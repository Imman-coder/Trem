package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.model.CourseCoverageDetailsDto


sealed class CourseCoverageDetailResult {
    data class Success(val data:CourseCoverageDetailsDto): CourseCoverageDetailResult()
    data class Failed(val exception: Exception): CourseCoverageDetailResult()
}