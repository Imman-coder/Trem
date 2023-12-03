package com.immanlv.trem.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CourseCoverageDetailsDto(
    val list: List<CoverageDetailDto>,
    val lastUpdated: String = "",
)

@Serializable
data class CoverageDetailDto(
    val sl:Int,
    val date:String,
    val time:String,
    val topic:String,
    val roomNo:String,
    val status:String
)