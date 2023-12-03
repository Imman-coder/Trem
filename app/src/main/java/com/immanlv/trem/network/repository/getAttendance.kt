package com.immanlv.trem.network.repository

import com.immanlv.trem.data.util.ExtrasString
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.network.mapped.AttendanceResult
import com.immanlv.trem.network.mapped.UserStaResult
import com.immanlv.trem.network.model.AttendanceSubjects
import com.immanlv.trem.network.model.mapper.AttendanceMapper
import com.immanlv.trem.network.util.NetworkService
import org.jsoup.Jsoup
import javax.inject.Inject

class getAttendance
@Inject constructor(
    private val networkService: NetworkService
) {
    suspend operator fun invoke(sem: Int): Attendance {
        when (val sta = networkService.getUserSta()) {
            is UserStaResult.Success -> {
                when (val k =
                    networkService.getAttendence(sem = sem.toString(), studentCode = sta.data)) {
                    is AttendanceResult.Success -> {
                        val nrv = mutableListOf<AttendanceSubjects>()
                        k.data.attendanceList.forEach {
                            val link =
                                Jsoup.parse(it.courseCoverage).body().select("a").attr("href")
                            nrv.add(
                                it.copy(
                                    courseCoverage = link
                                ),
                            )
                        }
                        var m = AttendanceMapper.mapToDomainModel(k.data.copy(attendanceList = nrv))
                        val a = k.data.attendanceList[0].section
                        val b = m.extras.toMutableMap()
                        b[ExtrasString.SECTION] = a[a.length - 1].toString()
                        m = m.copy(
                            extras = b
                        )
                        return m
                    }

                    is AttendanceResult.Failed -> {
                        throw k.exception
                    }
                }
            }

            is UserStaResult.Failed -> {
                throw Exception()
            }
        }
    }
}