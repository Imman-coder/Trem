package com.immanlv.trem.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ScorecardDto(
    val cgpa: Float = -1f,
    val sems: List<SemDto> = listOf()
)


@Serializable
data class SemDto(
    val sem: Int,
    val sgpa: Float,
    val subjects: List<ScorecardSubjectDto> = listOf(),
)

@Serializable
data class ScorecardSubjectDto(
    val name: String,
    val code: String,
    val credit: Int,
    val grade: Char,
)
