package com.immanlv.trem.domain.use_case;

import com.immanlv.trem.domain.use_case.cases.GetAttendance
import com.immanlv.trem.domain.use_case.cases.GetProfile
import com.immanlv.trem.domain.use_case.cases.GetScorecard
import com.immanlv.trem.domain.use_case.cases.GetTimetable
import com.immanlv.trem.domain.use_case.cases.RefreshAttendance
import com.immanlv.trem.domain.use_case.cases.RefreshLastUpdated
import com.immanlv.trem.domain.use_case.cases.RefreshProfile
import com.immanlv.trem.domain.use_case.cases.RefreshScorecard
import com.immanlv.trem.domain.use_case.cases.RefreshTimetable

data class ProfileUseCases (
    val getProfile: GetProfile,
    val getAttendance: GetAttendance,
    val getTimetable: GetTimetable,
    val getScorecard: GetScorecard,
    val refreshScorecard: RefreshScorecard,
    val refreshLastUpdated: RefreshLastUpdated,
    val refreshProfile: RefreshProfile,
    val refreshAttendance: RefreshAttendance,
    val refreshTimetable: RefreshTimetable
)
