package com.example.myapplication.presentation.navigation.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BaseApplication
import com.example.myapplication.domain.model.AppPreferences
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.AttendanceSubject
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Result
import com.example.myapplication.domain.model.Timetable
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.presentation.navigation.login.LoginViewModel
import com.example.myapplication.repository.DataRepository
import com.example.myapplication.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val credentialsDataStore: DataStore<Credentials>,
    private val profileDataStore: DataStore<Profile>,
    private val profileRepository: ProfileRepository,
    private val dataRepository: DataRepository,
    private val settingDataStore: DataStore<AppPreferences>,
    private val app: BaseApplication
) : ViewModel() {

    var profile: Flow<Profile> = profileDataStore.data.map { it }


    private val _timetableState = mutableStateOf(DataUiState<Timetable>())
    val timetableState: State<DataUiState<Timetable>> = _timetableState

    private val _attendanceState = mutableStateOf(DataUiState<List<AttendanceSubject>>())
    val attendanceState: State<DataUiState<List<AttendanceSubject>>> = _attendanceState

    private val _resultState = mutableStateOf(DataUiState<Result>())
    val resultState: State<DataUiState<Result>> = _resultState

    lateinit var credentials: Credentials


    private val isLoggedIn = app.isLoggedIn

    init {
        viewModelScope.launch {
            credentialsDataStore.data.collect { userCredentials ->
                credentials = userCredentials
            }
        }
    }


    fun login() {
        if (!isLoggedIn) {
            viewModelScope.launch {
                try {
                    val p = credentials.let { profileRepository.Login(it.uid,it.pass) }
                    profileDataStore.updateData { p }
                    app.isLoggedIn = true
                } catch (e: LoginException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun fetchResult(){
        if (isLoggedIn) {
            if (_resultState.value.dataState == DataState.Cache) {
                viewModelScope.launch {
                    _resultState.value =
                        _resultState.value.copy(dataState = DataState.Fetching)
                    try {
                            val result = profileRepository.getResults()
                            updateResult(result = result)
                    } catch (_: Exception) {
                        _resultState.value =
                            _resultState.value.copy(error = Error.NetworkError)
                    }
                }
            }
        } else {
            _resultState.value = _resultState.value.copy(error = Error.NotLoggedIn)
        }
    }

    private suspend fun updateResult(result: Result) {
        if (result == Result())
            _resultState.value = _resultState.value.copy(error = Error.EmptyContentError)
        else {
            profileDataStore.updateData {
                it.copy(
                    result = result
                )
            }
            _resultState.value =
                _resultState.value.copy(data = result, dataState = DataState.Network)
        }
    }

    fun fetchAttendance(){
        if (isLoggedIn) {
            if (_attendanceState.value.dataState == DataState.Cache) {
                viewModelScope.launch {
                    _attendanceState.value =
                        _attendanceState.value.copy(dataState = DataState.Fetching)
                    try {
                        profile.collect{
                            val attendance = profileRepository.getAttendance(it.sem)
                            updateAttendance(attendance = attendance)
                        }
                    } catch (_: Exception) {
                        _attendanceState.value =
                            _attendanceState.value.copy(error = Error.NetworkError)
                    }
                }
            }
        } else {
            _attendanceState.value = _attendanceState.value.copy(error = Error.NotLoggedIn)
        }
    }

    private suspend fun updateAttendance(attendance: Attendance) {
        if (attendance == Attendance())
            _attendanceState.value = _attendanceState.value.copy(error = Error.EmptyContentError)
        else {
            profileDataStore.updateData {
                it.copy(
                    attendance = attendance.subs
                )
            }
            _attendanceState.value =
                _attendanceState.value.copy(data = attendance.subs, dataState = DataState.Network)
        }
    }


    fun fetchTimetable() {
        if (isLoggedIn) {
            if (_timetableState.value.dataState == DataState.Cache) {
                viewModelScope.launch {
                    _timetableState.value =
                        _timetableState.value.copy(dataState = DataState.Fetching)
                    try {
                        val timetable = dataRepository.getTable()
                        updateTimetable(timetable = timetable)
                    } catch (_: Exception) {
                        _timetableState.value =
                            _timetableState.value.copy(error = Error.NetworkError)
                    }
                }
            }
        } else {
            _timetableState.value = _timetableState.value.copy(error = Error.NotLoggedIn)
        }
    }

    private suspend fun updateTimetable(timetable: Timetable) {
        if (timetable == Timetable())
            _timetableState.value = _timetableState.value.copy(error = Error.EmptyContentError)
        else {
            profileDataStore.updateData {
                it.copy(
                    timetable = timetable
                )
            }
            _timetableState.value =
                _timetableState.value.copy(data = timetable, dataState = DataState.Network)
        }
    }

    data class DataUiState<T>(
        var dataState: DataState = DataState.NotAvailable,
        var error: Error? = null,
        val data: T? = null

    )

    enum class DataState {
        Network, Cache, NotAvailable, Fetching
    }

    sealed class Error {
        object NotLoggedIn : Error()
        object NetworkError : Error()
        object EmptyContentError : Error()
    }

}