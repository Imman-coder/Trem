package com.example.myapplication.presentation.navigation.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Result
import com.example.myapplication.domain.model.Timetable
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.network.exceptions.UpdateException
import com.example.myapplication.repository.DataRepository
import com.example.myapplication.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val profileStore: DataStore<Profile>,
    val credentialStore: DataStore<Credentials>,
    private val profileRepository: ProfileRepository,
    private val dataRepository: DataRepository,
) : ViewModel() {


    fun getLProfileStore() = profileStore

    private var sem = -1

    private var updatedProfile = false
    private var updatedAttendance = false
    private var updatedResult = false
    private var updatedTimetable = false


    suspend fun updateProfile(uid: String = "", pass: String = "") {
        if (!updatedProfile) {
            if (uid != "" && pass != "") {
                println("login with $uid , $pass")
                val k = profileRepository.Login(uid, pass)
                setProfile(k)
                println(k)
                sem = k.sem
                updatedProfile = true
                return
            }
//            else {
//                println("login with ..")
//                credentialStore.data.map {
//                    println("login with ${it.uid} , ${it.pass}")
//                    val k = profileRepository.Login(it.uid, it.pass)
//                    runBlocking { setProfile(k) }
//                    println(k)
//                    sem = k.sem
//                    updatedProfile = true
//                }
//                println("login with ..2")
//            }
        } else
            throw LoginException("already logged in ", "Already logged in",LoginException.Error.AlreadyLoggedIn)
    }

    suspend fun updateAttendence() {
        if (!updatedAttendance) {
            val attendence = profileRepository.getAttendance(sem)
            println(attendence)
            setAttendance(attendence)
            updatedAttendance = true
        }
    }


    suspend fun updateResult() {
        if (!updatedResult) {
            setResult(profileRepository.getResults())
            updatedResult = true
        }
    }

    suspend fun updateTimetable() {
        if (!updatedTimetable) {
            setTimetable(dataRepository.getTable("testTable.json"))
            updatedTimetable = true
        }
    }


    private suspend fun setProfile(profile: Profile) {
        profileStore.updateData {
            it.copy(
                name = profile.name,
                redgno = profile.redgno,
                phoneno = profile.phoneno,
                sem = profile.sem,
                program = profile.program,
            )
        }
    }

    private suspend fun setAttendance(attendance: Attendance) {
        if(attendance== Attendance())
            throw UpdateException("Attendence received is empty")

        profileStore.updateData {
            it.copy(
                attendance = attendance.subs
            )
        }
    }

    private suspend fun setTimetable(timetable: Timetable) {
        if(timetable== Timetable())
            throw UpdateException("timetable received is empty")

        profileStore.updateData {
            it.copy(
                timetable = timetable
            )
        }
    }

    private suspend fun setResult(result: Result) {
        if(result== Result())
            throw UpdateException("Result body is empty")
        profileStore.updateData {
            it.copy(
                result = result
            )
        }
    }


    suspend fun setCredentials(uid: String, pass: String) {
        credentialStore.updateData {
            it.copy(
                hasCredentials = true, uid = uid, pass = pass
            )
        }
    }
    suspend fun setFakeCredentials(sem:Int,program:String,branch:String) {
        credentialStore.updateData { Credentials(hasCredentials = true, isFakeLoggedIn = true) }
        profileStore.updateData { Profile(sem = sem, branch = branch, program = program) }
    }

}