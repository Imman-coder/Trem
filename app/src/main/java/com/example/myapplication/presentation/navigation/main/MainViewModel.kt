package com.example.myapplication.presentation.navigation.main

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BaseApplication
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Scorecard
import com.example.myapplication.domain.model.Timetable
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.presentation.generateLogTag
import com.example.myapplication.repository.DataRepository
import com.example.myapplication.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val credentialsDataStore: DataStore<Credentials>,
    private val profileDataStore: DataStore<Profile>,
    private val timetableDataStore: DataStore<Timetable>,
    private val scorecardDataStore: DataStore<Scorecard>,
    private val attendanceDataStore: DataStore<Attendance>,

    private val profileRepository: ProfileRepository,
    private val dataRepository: DataRepository,

    val app: BaseApplication
) : ViewModel() {

    private val TAG = generateLogTag(MainViewModel::class.java.simpleName)


    private val _credentials = MutableStateFlow<Credentials?>(null)
    val credentials = _credentials.asStateFlow()

    private val _profileState = MutableStateFlow(DataState<Profile>())
    val profileState = _profileState.asStateFlow()

    private val _timetableState = MutableStateFlow(DataState<Timetable>())
    val timetableState = _timetableState.asStateFlow()

    private val _scorecardState = MutableStateFlow(DataState<Scorecard>())
    val scorecardState = _scorecardState.asStateFlow()

    private val _attendanceState = MutableStateFlow(DataState<Attendance>())
    val attendanceState = _attendanceState.asStateFlow()

    private val _hasCredentials = MutableStateFlow(true)
    val hasCredentials = _hasCredentials.asStateFlow()

    init {
        loadCredentials()
    }

    private fun loadCredentials() {
        viewModelScope.launch {
            println("loading values")
            credentialsDataStore.data.collect { userData ->
                _credentials.value = userData
                _hasCredentials.value = !((!userData.hasCredentials) || (userData.isFakeLoggedIn))
            }
        }
        viewModelScope.launch {
            println("loading profile")
            profileDataStore.data.collect { profile ->
                if (profile != Profile()) {
                    _profileState.value =
                        DataState(
                            dataBy = DataState.DataBy.Cache,
                            DataState.DataState.Idle,
                            profile
                        )
                }
            }
        }
        viewModelScope.launch {
            println("loading attendance")
            attendanceDataStore.data.collect { attendance ->
                if (attendance != Attendance()) {
                    _attendanceState.value =
                        DataState(
                            dataBy = DataState.DataBy.Cache,
                            DataState.DataState.Idle,
                            attendance
                        )
                }
            }
        }
        viewModelScope.launch {
            println("loading scorecard")
            scorecardDataStore.data.collect { scorecard ->
                if (scorecard != Scorecard()) {
                    _scorecardState.value =
                        DataState(
                            dataBy = DataState.DataBy.Cache,
                            DataState.DataState.Idle,
                            scorecard
                        )
                }
            }
        }
        viewModelScope.launch {
            println("loading timetable")
            timetableDataStore.data.collect { timetable ->
                if (timetable != Timetable()) {
                    _timetableState.value = DataState(
                        dataBy = DataState.DataBy.Cache,
                        DataState.DataState.Idle,
                        timetable
                    )
                }
            }
        }
    }

    suspend fun login() {
        if (!app.isLoggedIn) {
            _profileState.value =
                _profileState.value.copy(dataState = DataState.DataState.Fetching)
            try {
                val p = _credentials.value?.let { profileRepository.Login(it.uid, it.pass) }
                if (p != null) {
                    profileDataStore.updateData { p }
                    _profileState.value = _profileState.value.copy(
                        dataBy = DataState.DataBy.Network, data = p
                    )
                }
                app.isLoggedIn = true
            } catch (e: LoginException) {
                e.printStackTrace()
            }
            _profileState.value = _profileState.value.copy(
                dataState = DataState.DataState.Idle,
            )
        }
    }


    suspend fun fetchResult() {
        _scorecardState.value = _scorecardState.value.copy(
            dataState = DataState.DataState.Fetching,
        )
        if (app.isLoggedIn) {
            Log.d(TAG, "fetchResult: started")
            try {
                val result = profileRepository.getResults()
                Log.d(TAG, "fetchResult: fetched")
                updateResult(scorecard = result)
                Log.d("Application", "onCreate: result = $result")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            var k = 0
            while (k++ < 6 && !app.isLoggedIn) {
                login()
            }
            if (app.isLoggedIn) {
                fetchResult()
            } else {
                Log.d(TAG, "fetchAttendance: Login Failed")
            }
        }
        _scorecardState.value = _scorecardState.value.copy(
            dataState = DataState.DataState.Idle,
        )
    }

    private suspend fun updateResult(scorecard: Scorecard) {
        if (scorecard == Scorecard())
            TODO()
        else {
            scorecardDataStore.updateData {
                scorecard
            }
            _scorecardState.value = _scorecardState.value.copy(
                data = scorecard,
            )
        }
    }


    suspend fun fetchAttendance() {
        _attendanceState.value = _attendanceState.value.copy(
            dataState = DataState.DataState.Fetching,
        )
        if (app.isLoggedIn) {
            try {
                val attendance = profileState.value.data?.let {
                    profileRepository.getAttendance(it.sem)
                }
                if (attendance != null) {
                    updateAttendance(attendance = attendance)
                    _attendanceState.value = _attendanceState.value.copy(
                        data = attendance,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            var k = 0
            while (k++ < 6 && !app.isLoggedIn) {
                login()
            }
            if (app.isLoggedIn) {
                fetchAttendance()
            } else {
                Log.d(TAG, "fetchAttendance: Login Failed")
            }
        }
        _attendanceState.value = _attendanceState.value.copy(
            dataState = DataState.DataState.Idle,
        )
    }

    private suspend fun updateAttendance(attendance: Attendance) {
        if (attendance == Attendance())
            TODO()
        else {
            attendanceDataStore.updateData {
                attendance
            }
            _attendanceState.value = _attendanceState.value.copy(
                data = attendance,
            )
        }
    }


    suspend fun fetchTimetable() {
        _timetableState.value = _timetableState.value.copy(
            dataState = DataState.DataState.Fetching,
        )
        try {
            Log.d(TAG, "fetchTimetable: Fetching Timetable ")
            val timetable = dataRepository.getTable()
            updateTimetable(timetable = timetable)
        } catch (e: Exception) {
            e.printStackTrace()

        }
        _timetableState.value = _timetableState.value.copy(
            dataState = DataState.DataState.Idle,
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
                data = timetable,
            )
        }
    }

    data class DataState<T>(
        val dataBy: DataBy = DataBy.NotAvailable,
        val dataState: DataState = DataState.Idle,
        val data: T? = null
    ) {
        sealed class DataBy {
            object Cache : DataBy()
            object Network : DataBy()
            object NotAvailable : DataBy()
        }

        sealed class DataState {
            object Fetching : DataState()
            object Idle : DataState()
        }

    }

}