package com.example.myapplication.network.model

import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Result
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto (
    val name: String = "",
    val redgno : Long = -1,
    val phoneno : Long = -1,
    val sem : Int = -1,
    val program : String = "",
    val attendance: Attendance? = null,
    val result: Result? = null
)