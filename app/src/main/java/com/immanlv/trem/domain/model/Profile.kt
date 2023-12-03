package com.immanlv.trem.domain.model

import com.immanlv.trem.domain.util.DataErrorType
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val name: String = "",
    val regdno: Long = -1,
    val phoneno: Long = -1,
    val sem: Int = -1,
    val program: String = "",
    val branch: String = "",
    val batch: String = "",
    val sicno:Long = -1,
    val section: Char = ' ',
    val propic: String? = null,
    val propicError: DataErrorType = DataErrorType.NoError,
    val extras: Map<String,String> = mapOf()
)