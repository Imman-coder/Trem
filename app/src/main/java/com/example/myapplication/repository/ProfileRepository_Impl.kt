package com.example.myapplication.repository

import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Result
import com.example.myapplication.domain.model.ResultSubject
import com.example.myapplication.domain.model.Sem
import com.example.myapplication.network.ProfileService
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.network.response.FetchType
import com.example.myapplication.network.util.AttendenceDtoMapper
import com.example.myapplication.network.util.ProfileDtoMapper
import com.example.myapplication.network.util.ResultDtoMapper
import com.example.myapplication.network.util.encrypt
import com.example.myapplication.network.util.gradeMapper
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
                throw LoginException("Unable to fetch Hash Key", "Please try again after somtime")

            val shaPass = encrypt.getSha512(encrypt.getSha512("$id#$password") + "#${hash.data}")
            val pass = encrypt.getMd5(encrypt.getMd5("$id#$password") + "#${hash.data}")


            val response = profileService.login("DGI", hash.data, pass, shaPass, id)

            if (response.type == FetchType.Successful && response.data != null)
                return profileMapper.mapToDomainModel(response.data)
            else
                throw LoginException("Login Failed", "Invalid Credentials")
        } catch (e: SocketTimeoutException) {
            throw LoginException("Failed to connect to the Internet", "No Intenet")
        }
    }

    override suspend fun getAttendance(sem: Int): Attendance {

        val sta = profileService.getUserSta()

        if (sta.type == FetchType.Successful && sta.usercode != null) {
            return attendenceMapper.mapToDomainModel(profileService.getAttendence(
                sem = sem.toString(),
                studentCode = sta.usercode
            ))
        }


        return Attendance()
    }

    override suspend fun getResults(): Result {
        return resultMapper.mapToDomainModel(profileService.getResults())
    }
}