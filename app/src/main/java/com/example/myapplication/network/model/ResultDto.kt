package com.example.myapplication.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ResultDto(
    val cgpa: Float = -1f,
    val sems: List<SemDto> = listOf()
)


@Serializable
data class SemDto(
    val sem: Int,
    val sgpa: Float,
    val subjects: List<ResultSubjectDto> = listOf(),
)

@Serializable
data class ResultSubjectDto(
    val name: String,
    val code: String,
    val credit: Int,
    val grade: Char,
)
