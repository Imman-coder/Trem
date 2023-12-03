package com.immanlv.trem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Scorecard(
    val cgpa:Float = -1f,
    val sems :List<Sem> = listOf()
)


@Serializable
data class Sem(
    val sem:Int,
    val sgpa:Float,
    val subjects : List<ResultSubject> = listOf(),
)

@Serializable
data class ResultSubject(
    val name:String,
    val code:String,
    val credit:Int,
    val grade: Grade = Grade.F,
)

enum class Grade{
    F,D,C,B,A,E,O
}