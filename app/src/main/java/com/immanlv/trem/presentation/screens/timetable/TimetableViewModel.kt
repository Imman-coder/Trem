package com.immanlv.trem.presentation.screens.timetable

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.use_case.ProfileUseCases
import com.immanlv.trem.presentation.screens.timetable.util.SubjectSummaryHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _timetable = mutableStateOf(Timetable())
    val timetable: State<Timetable> = _timetable

    private val _attendance = mutableStateOf(Attendance())
    val attendance: State<Attendance> = _attendance

    private val _timetableScreenState = mutableStateOf(TimetableScreenState())
    val timetableScreenState: State<TimetableScreenState> = _timetableScreenState

    private var getTimetableJob: Job? = null
    private var getAttendanceJob: Job? = null

    init {
        getTimetable()
        getAttendance()
    }

    fun onEvent(event: TimetableScreenEvent) {
        when (event) {
            is TimetableScreenEvent.RefreshTimetable -> refreshTimetable()
            is TimetableScreenEvent.ShowClassCard -> showClassCard(event.subCode)
            TimetableScreenEvent.HideClassCard -> hideClassCard()
        }
    }


    private fun hideClassCard() {
        _timetableScreenState.value = _timetableScreenState.value.copy(
            classCardState = ClassCardState.Idle
        )
    }

    private fun showClassCard(subCode: String) {
        _timetableScreenState.value = _timetableScreenState.value.copy(
            classCardState = ClassCardState.ShowClassDetailCardState(
                SubjectSummaryHolder(
                    timetable.value.getSummaryOfClass(subCode),
                    attendance.value.getAttendanceOf(subCode)
                )
            )
        )

    }


    private fun refreshTimetable() {
        viewModelScope.launch {
            _timetableScreenState.value =
                _timetableScreenState.value.copy(timetableState = TimetableState.Loading.Fetching)
            profileUseCases.refreshTimetable()
            _timetableScreenState.value =
                _timetableScreenState.value.copy(timetableState = TimetableState.Loading.Retrieving)
            _timetableScreenState.value =
                _timetableScreenState.value.copy(timetableState = TimetableState.Idle)
        }
    }

    fun getTimetable() {
        getTimetableJob?.cancel()
        viewModelScope.launch {
            getTimetableJob = profileUseCases.getTimetable().onEach {
                _timetable.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun getAttendance() {
        getAttendanceJob?.cancel()
        viewModelScope.launch {
            getAttendanceJob = profileUseCases.getAttendance().onEach {
                _attendance.value = it
            }.launchIn(viewModelScope)
        }
    }
}