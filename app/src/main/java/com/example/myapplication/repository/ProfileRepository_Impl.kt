package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Result
import com.example.myapplication.network.ProfileService
import com.example.myapplication.network.exceptions.FetchException
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.network.response.FetchType
import com.example.myapplication.network.util.AttendenceDtoMapper
import com.example.myapplication.network.util.ProfileDtoMapper
import com.example.myapplication.network.util.ResultDtoMapper
import com.example.myapplication.network.util.encrypt
import java.net.SocketTimeoutException

class ProfileRepository_Impl(
    private val profileService: ProfileService,
    private val profileMapper: ProfileDtoMapper,
    private val attendenceMapper: AttendenceDtoMapper,
    private val resultMapper: ResultDtoMapper,
) : ProfileRepository {
    override suspend fun Login(id: String, password: String): Profile {
        try {
            val hash = profileService.getHash()

            if (hash.type == FetchType.Unsuccessful)
                throw LoginException(
                    "Unable to fetch Hash Key",
                    "Unable to fetch HashKey",
                    LoginException.Error.NetworkError
                )

            val shaPass = encrypt.getSha512(encrypt.getSha512("$id#$password") + "#${hash.data}")
            val pass = encrypt.getMd5(encrypt.getMd5("$id#$password") + "#${hash.data}")


            val response = profileService.login("DGI", hash.data, pass, shaPass, id)

            if (response.type == FetchType.Successful && response.data != null)
                return profileMapper.mapToDomainModel(response.data)
            else
                throw LoginException(
                    "Login Failed", "Invalid Credentials",
                    LoginException.Error.InvalidCredentials
                )
        } catch (e: SocketTimeoutException) {
            throw LoginException(
                "Failed to connect to the Internet", "No Intenet",
                LoginException.Error.NetworkError
            )
        }
    }

    override suspend fun getAttendance(sem: Int): Attendance {

        val sta = profileService.getUserSta()

        if (sta.type == FetchType.Successful && sta.usercode != null) {
            val attendance = profileService.getAttendence(
                sem = sem.toString(),
                studentCode = sta.usercode
            )
            return attendenceMapper.mapToDomainModel(attendance)
        }
        Log.e("Sta", sta.toString())
        throw FetchException("Unable to fetch userStableData", "Failed to fetch Attendence")
        return Attendance()
    }

    override suspend fun getResults(): Result {
        return resultMapper.mapToDomainModel(profileService.getResults())
    }
}