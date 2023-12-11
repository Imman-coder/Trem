package com.immanlv.trem.presentation.screens.timetable

import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.domain.model.TimetableStat
import com.immanlv.trem.presentation.screens.timetable.util.SubjectSummaryHolder

data class TimetableScreenState(
    val timetableState:TimetableState = TimetableState.Idle,
    val classCardState: ClassCardState = ClassCardState.Idle
)

sealed class TimetableState {
    data object Idle : TimetableState()
    sealed class Loading {
        data object Fetching : TimetableState()
        data object Retrieving : TimetableState()
    }
    data class Error(val error: Exception) : TimetableState()
}

sealed class ClassCardState{
    data object Idle:ClassCardState()
    data class ShowClassDetailCardState(
        val data: SubjectSummaryHolder
    ) : ClassCardState()
}

