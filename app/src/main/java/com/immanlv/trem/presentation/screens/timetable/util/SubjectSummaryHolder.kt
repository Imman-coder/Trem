package com.immanlv.trem.presentation.screens.timetable.util

import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.domain.model.TimetableStat

data class SubjectSummaryHolder(
    val timetableStat: TimetableStat?,
    val attendanceSubject: AttendanceSubject?
)
