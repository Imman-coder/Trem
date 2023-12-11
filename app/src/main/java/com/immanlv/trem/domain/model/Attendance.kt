package com.immanlv.trem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Attendance(
    val total: Float = 0f,
    val subs: List<AttendanceSubject> = listOf(),
    val extras : Map<String,String> = mapOf(),
){
    fun getAttendanceOf(subCode:String):AttendanceSubject?{
        subs.forEach {
            if(it.code == subCode) return it
        }
        return null
    }
}

@Serializable
data class AttendanceSubject(
    val name: String,
    val absent: Int,
    val present: Int,
    val conducted: Int,
    val code :String,
    val type :ClassType,
    val credit :Int,
    val courseCoverageLink :String = "",
    val lastUpdated :String = "-",
    val classDetails :List<CoverageDetail> = listOf()
)

