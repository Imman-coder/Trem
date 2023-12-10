package com.immanlv.trem.presentation.screens.timetable

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
):ViewModel() {

    private val _timetable = mutableStateOf(Timetable())
    val timetable : State<Timetable> = _timetable

    private val _timetableState = mutableStateOf<TimetableState>(TimetableState.Idle)
    val timetableState : State<TimetableState> = _timetableState

    private var getTimetableJob : Job? = null

    init {
        getTimetable()
    }

    fun onEvent(event:TimetableScreenEvent){
        when(event){
            is TimetableScreenEvent.RefreshTimetable -> refreshTimetable()
        }
    }

    private fun refreshTimetable(){
        viewModelScope.launch {
            _timetableState.value = TimetableState.Loading.Fetching
            profileUseCases.refreshTimetable()
            _timetableState.value = TimetableState.Loading.Retrieving
            _timetableState.value = TimetableState.Idle
        }
    }

    fun getTimetable(){
        getTimetableJob?.cancel()
        viewModelScope.launch {
            getTimetableJob = profileUseCases.getTimetable().onEach {
                _timetable.value = it
            }.launchIn(viewModelScope)
        }

    }

}