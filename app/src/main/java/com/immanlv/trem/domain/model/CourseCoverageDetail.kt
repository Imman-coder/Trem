package com.immanlv.trem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CourseCoverageDetails(
    val list: List<CoverageDetail>,
    val lastUpdate:String = if( list.isNotEmpty() ) list.last().date else "Not Updated",
)

@Serializable
data class CoverageDetail(
    val sl:Int,
    val date:String,
    val time:String,
    val topic:String,
    val roomNo:String,
    val status:ClassStatus = ClassStatus.Absent
)

enum class ClassStatus{
    Present, Absent
}