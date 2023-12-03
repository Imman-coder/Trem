package com.immanlv.trem.network.repository

import com.immanlv.trem.data.model.LoginStatusE
import com.immanlv.trem.data.util.NetworkRequests
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.CourseCoverageDetails
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.presentation.screens.settings.SettingScreenEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkRequestsImpl
@Inject constructor(
    private val _login: login,
    private val _getLoginStatus: getLoginStatus,
    private val _getUserInfo: getUserInfo,
    private val _getProfilePicture: getProfilePicture,
    private val _getProfilePictureByUrl: getProfilePictureByUrl,
    private val _getCourseCoverageDetail: getCourseCoverageDetail,
    private val _getAttendance: getAttendance,
    private val _getResults: getResults,
    private val _getTimetable: getTimetable,
    private val _getTestTimetable: getTestTimetable,
    private val _logout: logout
) : NetworkRequests {
    override suspend fun login(id: String, password: String): Flow<LoginStatusE> =
        _login(id, password)

    override suspend fun logout() {
        _logout()
    }


    override suspend fun getLoginStatus(): Boolean = _getLoginStatus()

    override suspend fun getUserDetails(): Profile = _getUserInfo()

    override suspend fun getProfilePicture(
        program: String,
        batch: String,
        branch: String,
        sicno: Long
    ) = _getProfilePicture(program, batch, branch, sicno)


    override suspend fun getProfilePicture(url: String): String = _getProfilePictureByUrl(url)
    override suspend fun getCourseCoverage(
        subjectCode: String,
        sem: Int,
        branch: String,
        section: Char,
        program: String,
        studentCode: Long,
        batch: String
    ): CourseCoverageDetails = _getCourseCoverageDetail(
        subjectCode = subjectCode,
        sem = sem,
        branch = branch,
        section = section,
        program = program,
        studentCode = studentCode,
        batch = batch,
    )

    override suspend fun getAttendance(sem: Int): Attendance = _getAttendance(sem)

    override suspend fun getResults(): Scorecard = _getResults()

    override suspend fun getTimetable(
        program: String,
        batch: String,
        branch: String,
        section: Char
    ): Timetable = _getTimetable(program, batch, branch, section)

    override suspend fun getTestTimetable(): Timetable = _getTestTimetable()
}