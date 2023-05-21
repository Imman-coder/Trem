package com.example.myapplication.domain.model

import kotlinx.collections.immutable.PersistentCollection
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class Attendance(
    val total: Float = 0f,
    val subs: List<AttendanceSubject> = listOf(),
)

@Serializable
data class AttendanceSubject(
    val name: String,
    val absent: Int,
    val present: Int,
    val conducted: Int,
    val code :String,

)