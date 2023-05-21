package com.example.myapplication.network.response

import com.example.myapplication.network.model.ProfileDto

data class ProfileCarrier(
    val type : FetchType = FetchType.Unsuccessful,
    val data : ProfileDto? = null
)
data class HashCarrier(
    val type : FetchType = FetchType.Unsuccessful,
    val data : String = ""
)
data class AttendenceCarrier(
    val type : FetchType = FetchType.Unsuccessful,
    val data : String = ""
)

data class UserSta(
    val type: FetchType=FetchType.Unsuccessful,
    val usercode:String? = null
)

enum class FetchType {
    Successful,
    Unsuccessful
}