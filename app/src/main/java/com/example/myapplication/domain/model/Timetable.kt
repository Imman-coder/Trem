package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class Timetable(
    val EventTable: List<List<Int>> = listOf(),
    val TimeList: List<Int> = listOf(),
    val EventList: List<Event> = listOf()
)

@Serializable
data class Event(
    val time_span: Int,
    val subjects: List<Subject>,
    val class_type: ClassType
)

@Serializable
data class Subject(
    val subject: String,
    val subject_code: String,
    val teacher: String
)

enum class ClassType {
    Theory,Lab,Notice
}