package com.immanlv.trem.data.repository

import android.util.Log
import com.google.gson.Gson
import com.immanlv.trem.data.data_source.AttendanceDao
import com.immanlv.trem.data.data_source.ProfileDao
import com.immanlv.trem.data.data_source.ScorecardDao
import com.immanlv.trem.data.data_source.TimetableDao
import com.immanlv.trem.data.util.ExtrasString
import com.immanlv.trem.data.util.NetworkRequests
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.repository.AppDataRepository
import com.immanlv.trem.domain.repository.LoginDataRepository
import com.immanlv.trem.domain.repository.ProfileDataRepository
import com.immanlv.trem.domain.util.DataErrorType
import com.immanlv.trem.network.model.TimetableDto
import com.immanlv.trem.network.model.mapper.TimetableMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ProfileDataRepositoryImpl
@Inject constructor(
    private val profileDao: ProfileDao,
    private val attendanceDao: AttendanceDao,
    private val timetableDao: TimetableDao,
    private val scorecardDao: ScorecardDao,
    private val loginDataRepository: LoginDataRepository,
    private val networkRequests: NetworkRequests,
    private val appDataRepository: AppDataRepository,


    ) : ProfileDataRepository {
    override suspend fun getProfile(): Flow<Profile> {
        if (profileDao.getCredentials().hasCredentials) return profileDao.getProfile()
        refreshProfile()
        return profileDao.getProfile()
    }


    override suspend fun refreshProfile() {
        val ls = networkRequests.getLoginStatus()
        Log.d("TAG", "refreshProfile: Get Login Status $ls")
        if (!ls) {
            Log.d("TAG", "refreshProfile: Attempting Login")
            loginDataRepository.login()
        }
        var profile = networkRequests.getUserDetails()
        if ((profile.extras[ExtrasString.PROFILEPICTURE_URL]?.length ?: 0) > 20) {
            profile = try {
//                val propic =
//                    networkRequests.getProfilePicture(profile.extras[ExtrasString.PROFILEPICTURE_URL]!!)
                val propic =
                    networkRequests.getProfilePicture(
                        program = profile.program,
                        batch = profile.batch,
                        branch = profile.branch,
                        sicno = profile.sicno,
                    )
                profile.copy(propic = propic)
            } catch (_: Exception) {
                profile.copy(propicError = DataErrorType.NoDataFound)
            }
        }
        profileDao.saveProfile(profile)
        refreshAttendance()
    }

    override suspend fun getAttendance(): Flow<Attendance> {
        var hasData: Boolean
        runBlocking {
            hasData = attendanceDao.getAttendance()
                .first() != Attendance()
        }
        if (hasData) return attendanceDao.getAttendance()
        refreshAttendance()
        return attendanceDao.getAttendance()
    }

    override suspend fun refreshAttendance() {
        if (!networkRequests.getLoginStatus()) {
            loginDataRepository.login()
        }
        var profile: Profile
        runBlocking(Dispatchers.IO) {
            if (profileDao.getProfile().first() == Profile()) refreshProfile()
            profile = getProfile().first()
        }
        val attendance = networkRequests.getAttendance(profile.sem)
        if ((attendance.extras[ExtrasString.SECTION]?.length ?: 0) > 0) {
            var a = profileDao.getProfile().first()
            a = a.copy(
                section = attendance.extras[ExtrasString.SECTION]?.get(0) ?: ' '
            )
            profileDao.saveProfile(a)
        }

        attendanceDao.saveAttendance(attendance)

        if (appDataRepository.getAppPreference().first().autoFetchLastUpdated) {
            attendance.subs.forEach {
                refreshLastUpdated(it.code)
            }
        }

    }

    override suspend fun refreshLastUpdated(subjectCode: String) {
        if (!networkRequests.getLoginStatus()) {
            loginDataRepository.login()
        }

        var attendance = attendanceDao.getAttendance().first()

        var profile: Profile
        runBlocking(Dispatchers.IO) {
            if (profileDao.getProfile().first() == Profile()) refreshProfile()
            profile = getProfile().first()
        }

        val newSubs = mutableListOf<AttendanceSubject>()
        attendance.subs.forEach { sub ->
            if (subjectCode == sub.code) {
                val l = networkRequests.getCourseCoverage(
                    subjectCode = sub.code,
                    sem = profile.sem,
                    branch = profile.branch,
                    section = profile.section,
                    program = profile.program,
                    studentCode = profile.sicno,
                    batch = profile.batch,
                )
                newSubs += sub.copy(
                    classDetails = l.list,
                    lastUpdated = l.lastUpdate
                )
            } else {
                newSubs += sub
            }
        }
        attendance = attendance.copy(subs = newSubs)
        attendanceDao.saveAttendance(attendance)
    }


    override suspend fun getScorecard(): Flow<Scorecard> {
        if (scorecardDao.getScorecard().first() != Scorecard()) return scorecardDao.getScorecard()
        refreshScorecard()
        return scorecardDao.getScorecard()
    }

    override suspend fun refreshScorecard() {
        if (!networkRequests.getLoginStatus()) {
            loginDataRepository.login()
        }
        try {
            val scorecard = networkRequests.getResults()
            scorecardDao.saveScorecard(scorecard)
        } catch (_: Exception) {
            scorecardDao.saveScorecard(Scorecard(error = DataErrorType.NoDataFound))
        }
    }

    override suspend fun getTimetable(): Flow<Timetable> {
        if (timetableDao.getTimetable().first() != Timetable()) return timetableDao.getTimetable()
        if (timetableDao.getTimetable().first().error != DataErrorType.NoDataFound)
            refreshTimetable()
        return timetableDao.getTimetable()

    }

    override suspend fun refreshTimetable() {
        try {
            Log.d("TAG", "refreshTimetable: Started")
            val s = appDataRepository.getAppPreference().first().loadBuilderTimetable
            val timetable: Timetable =
                if (s.isNotEmpty()) {
                    Gson().fromJson(
                        s,
                        Timetable::class.java
                    )
                } else
                    when (val v =
                        appDataRepository.getAppPreference().first().loadCustomTimetable) {
                        "" -> {
                            Log.d("TAG", "refreshTimetable: No Custom")
                            var profile: Profile
                            runBlocking(Dispatchers.IO) {
                                if (getProfile().first() == Profile()) {
                                    if (!networkRequests.getLoginStatus())
                                        loginDataRepository.login()
                                    refreshProfile()
                                }
                                profile = getProfile().first()
                            }
                            networkRequests.getTimetable(
                                profile.program,
                                profile.batch,
                                profile.branch,
                                profile.section
                            )
                        }

                        "test", "Test", "T", "t" -> {
                            Log.d("TAG", "refreshTimetable: Loading Test Timetable")
                            networkRequests.getTestTimetable()
                        }

                        else -> {

                            if (v.substring(0, 10).contains("inject")) {
                                Log.d("TAG", "refreshTimetable: Loading injected Timetable")
                                val gson = Gson()
                                val js = v.substring(v.indexOf(":") + 1).trim()
                                try {
                                    TimetableMapper.mapToDomainModel(
                                        gson.fromJson(
                                            js,
                                            TimetableDto::class.java
                                        )
                                    )
                                } catch (_: Exception) {
                                    gson.fromJson(js, Timetable::class.java)
                                }

                            } else {
                                Log.d("TAG", "refreshTimetable: Loading implicit Timetable")
                                // Program,Batch,Branch,Section
                                val ar = v.split(",")
                                networkRequests.getTimetable(
                                    ar[0],
                                    ar[1],
                                    ar[2],
                                    ar[3][0]
                                )
                            }

                        }
                    }
            timetableDao.saveTimetable(timetable)
        } catch (err: Exception) {
            timetableDao.saveTimetable(Timetable(error = DataErrorType.NoDataFound))
        }
    }
}