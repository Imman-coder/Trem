package com.immanlv.trem.domain.repository

import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import kotlinx.coroutines.flow.Flow

interface ProfileDataRepository {
    suspend fun getProfile():Flow<Profile>

    suspend fun refreshProfile()

    suspend fun getAttendance():Flow<Attendance>

    suspend fun refreshAttendance()

    suspend fun refreshLastUpdated(subjectCode:String)

    suspend fun getScorecard():Flow<Scorecard>

    suspend fun refreshScorecard()

    suspend fun getTimetable():Flow<Timetable>

    suspend fun refreshTimetable()
}