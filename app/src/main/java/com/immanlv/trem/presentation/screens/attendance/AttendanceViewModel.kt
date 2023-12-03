package com.immanlv.trem.presentation.screens.attendance

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel
@Inject
constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {
    private val _attendanceState = mutableStateOf<AttendanceState>(AttendanceState.Idle)
    val attendanceState: State<AttendanceState> = _attendanceState

    private val _attendance = mutableStateOf(Attendance())
    val attendance: State<Attendance> = _attendance

    private var getAttendanceJob: Job? = null

    init {
        getAttendance()
    }


    fun onEvent(event: AttendanceViewEvent) {
        when (event) {
            AttendanceViewEvent.GetAttendanceView -> {
                getAttendance()
            }

            AttendanceViewEvent.RefreshAttendanceView -> {
                viewModelScope.launch {
                    _attendanceState.value = AttendanceState.Loading.Fetching
                    profileUseCases.refreshAttendance()
                    _attendanceState.value = AttendanceState.Loading.Retrieving
                    _attendanceState.value = AttendanceState.Idle
                }
            }

            is AttendanceViewEvent.RefreshLateUpdated -> {
                viewModelScope.launch {
                    profileUseCases.refreshLastUpdated(event.code)
                }
            }
        }
    }

    private fun getAttendance() {
        getAttendanceJob?.cancel()
        viewModelScope.launch {
            getAttendanceJob = profileUseCases.getAttendance().onEach {
                _attendance.value = _attendance.value.copy(
                    total = it.total,
                    subs = it.subs
                )
            }.launchIn(viewModelScope)
        }
    }

}