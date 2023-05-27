package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class Profile (
    val name: String = "",
    val redgno : Long = -1,
    val phoneno : Long = -1,
    val sem : Int = -1,
    val program : String = "",
    val attendance: List<AttendanceSubject> = listOf(),
    val result: Result = Result(),
    val timetable: Timetable = Timetable()
)