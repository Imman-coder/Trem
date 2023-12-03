package com.immanlv.trem.presentation.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel
@Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _profile = mutableStateOf(Profile())
    val profile: State<Profile> = _profile

    private val _timetable = mutableStateOf(Timetable())
    val timetable : State<Timetable> = _timetable


    private var getProfileJob : Job? = null
    private var getTimetableJob : Job? = null


    init {
        getProfile()
        getTimetable()
    }

    private fun getProfile(){
        getProfileJob?.cancel()
        viewModelScope.launch {
            getProfileJob = profileUseCases.getProfile().onEach {
                _profile.value = it
            }.launchIn(viewModelScope)
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