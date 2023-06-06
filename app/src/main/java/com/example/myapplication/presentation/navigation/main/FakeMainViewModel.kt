package com.example.myapplication.presentation.navigation.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Timetable
import com.example.myapplication.presentation.generateLogTag
import com.example.myapplication.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FakeMainViewModel @Inject constructor(
    private val credentialsDataStore: DataStore<Credentials>,
    private val timetableDataStore: DataStore<Timetable>,

    private val dataRepository: DataRepository,
) : ViewModel() {

    private val TAG = generateLogTag(FakeMainViewModel::class.java.simpleName)


    private val _timetableState = MutableStateFlow(MainViewModel.DataState<Timetable>())
    val timetableState = _timetableState.asStateFlow()

    private val _hasCredentials = MutableStateFlow(true)
    val hasCredentials = _hasCredentials.asStateFlow()

    init {
        loadCredentials()
    }

    private fun loadCredentials() {
        viewModelScope.launch {
            println("loading values")
            credentialsDataStore.data.collect { userData ->
                _hasCredentials.value = userData.isFakeLoggedIn
            }
        }
        viewModelScope.launch {
            timetableDataStore.data.collect { timetable ->
                if (timetable != Timetable()) {
                    _timetableState.value = MainViewModel.DataState(
                        dataBy = MainViewModel.DataState.DataBy.Cache,
                        MainViewModel.DataState.DataState.Idle,
                        timetable
                    )
                }
            }
        }
    }


    suspend fun fetchTimetable() {
        try {
            _timetableState.value =
                _timetableState.value.copy(dataState = MainViewModel.DataState.DataState.Fetching)
            val timetable = dataRepository.getTable()
            updateTimetable(timetable = timetable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _timetableState.value = _timetableState.value.copy(
            dataState = MainViewModel.DataState.DataState.Idle
        )
    }

    private suspend fun updateTimetable(timetable: Timetable) {
        if (timetable == Timetable())
            TODO()
        else {
            timetableDataStore.updateData {
                timetable
            }
            _timetableState.value = _timetableState.value.copy(
                dataBy = MainViewModel.DataState.DataBy.Network
            )
        }
    }

}