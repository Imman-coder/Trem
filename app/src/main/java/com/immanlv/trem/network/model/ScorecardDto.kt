package com.immanlv.trem.network.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ScorecardDto(
    val cgpa: Float = -1f,
    val sems: List<SemDto> = listOf()
)


@Serializable
@Keep
data class SemDto(
    val sem: Int,
    val sgpa: Float,
    val subjects: List<ScorecardSubjectDto> = listOf(),
)

@Serializable
@Keep
data class ScorecardSubjectDto(
    val name: String,
    val code: String,
    val credit: Int,
    val grade: Char,
)
