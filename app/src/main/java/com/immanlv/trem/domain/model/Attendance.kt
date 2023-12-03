package com.immanlv.trem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Attendance(
    val total: Float = 0f,
    val subs: List<AttendanceSubject> = listOf(),
    val extras : Map<String,String> = mapOf(),
)

@Serializable
data class AttendanceSubject(
    val name: String,
    val absent: Int,
    val present: Int,
    val conducted: Int,
    val code :String,
    val type :SubjectType,
    val credit :Int,
    val courseCoverageLink :String = "",
    val lastUpdated :String = "-",
    val classDetails :List<CoverageDetail> = listOf()
)

enum class SubjectType{
    THEORY,LAB
}
