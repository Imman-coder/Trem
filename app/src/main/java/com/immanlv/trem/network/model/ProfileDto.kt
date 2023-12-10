package com.immanlv.trem.network.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ProfileDto (
    val name: String = "",
    val redgno : Long = -1,
    val phoneno : Long = -1,
    val sem : Int = -1,
    val program : String = "",
    val branch : String,
    val batch:String,
    val sicno:Long,
    val extras:Map<String,String> = mapOf()
)