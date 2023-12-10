package com.immanlv.trem.presentation.screens.profile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _profile = mutableStateOf(Profile())
    val profile: State<Profile> = _profile

    private val _scorecard = mutableStateOf(Scorecard())
    val scorecard: State<Scorecard> = _scorecard


    private var getProfileJob: Job? = null
    private var getScorecardJob: Job? = null


    init {
        getProfile()
        getScorecard()
    }

    fun onEvent(event: ProfileScreenEvent) {
        when (event) {
            ProfileScreenEvent.RefreshProfile -> refreshProfile()

            ProfileScreenEvent.RefreshScorecard -> refreshScorecard()
        }

    }

    private fun refreshProfile() {
        viewModelScope.launch {
            profileUseCases.refreshProfile()
        }
    }
    private fun refreshScorecard() {
        viewModelScope.launch {
            profileUseCases.refreshScorecard()
        }
    }

    private fun getProfile() {
        getProfileJob?.cancel()
        viewModelScope.launch {
            getProfileJob = profileUseCases.getProfile().onEach {
                _profile.value = it
            }.launchIn(viewModelScope)
        }
    }


    private fun getScorecard() {
        getScorecardJob?.cancel()
        viewModelScope.launch {
            getScorecardJob = profileUseCases.getScorecard().onEach {
                _scorecard.value = it
            }.launchIn(viewModelScope)
        }

    }
}