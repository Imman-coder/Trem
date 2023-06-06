package com.example.myapplication.repository

import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Scorecard

interface ProfileRepository {

    suspend fun Login(id: String, password: String): Profile

    suspend fun getAttendance(sem: Int):Attendance

    suspend fun getResults():Scorecard


}