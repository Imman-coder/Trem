package com.immanlv.trem.data.util

import com.immanlv.trem.data.model.LoginStatusE
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.CourseCoverageDetails
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import kotlinx.coroutines.flow.Flow


interface NetworkRequests {
    suspend fun login(id: String, password: String): Flow<LoginStatusE>
    suspend fun logout()
    suspend fun getLoginStatus(): Boolean
    suspend fun getUserDetails(): Profile
    suspend fun getProfilePicture(
        program: String,
        batch: String,
        branch: String,
        sicno: Long
    ): String

    suspend fun getCourseCoverage(
        subjectCode: String,
        sem: Int,
        branch: String,
        section: Char,
        program: String,
        studentCode: Long,
        batch: String
    ): CourseCoverageDetails

    suspend fun getProfilePicture(url: String): String
    suspend fun getAttendance(sem: Int): Attendance
    suspend fun getResults(): Scorecard
    suspend fun getTimetable(
        program: String,
        batch: String,
        branch: String,
        section: Char
    ): Timetable

    suspend fun getTestTimetable(): Timetable
}