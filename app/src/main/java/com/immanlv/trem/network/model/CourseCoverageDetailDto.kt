package com.immanlv.trem.network.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class CourseCoverageDetailsDto(
    val list: List<CoverageDetailDto>,
    val lastUpdated: String = "",
)

@Serializable
@Keep
data class CoverageDetailDto(
    val sl:Int,
    val date:String,
    val time:String,
    val topic:String,
    val roomNo:String,
    val status:String
)