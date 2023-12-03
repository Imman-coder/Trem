package com.immanlv.trem.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val hasCredentials: Boolean=false,
    val isFakeLogin: Boolean=false,
    val saveCredentials:Boolean = false,
    val uid: String = "",
    val pass: String = "",
)