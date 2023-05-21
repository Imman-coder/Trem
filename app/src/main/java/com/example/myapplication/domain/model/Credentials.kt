package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val hasCredentials: Boolean=false,
    val uid: String = "",
    val pass: String = "",
)
