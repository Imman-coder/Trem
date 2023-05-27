package com.example.myapplication.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableDto(
    val EventTable: List<List<Int>>,
    val TimeList: List<Int>,
    val EventList: Map<String, EventDto>
)

@Serializable
data class EventDto(
    val time_span: Int,
    val subjects: List<SubjectDto>,
    val class_type: Int
)

@Serializable
data class SubjectDto(
    val subject: String,
    val subject_code: String,
    val teacher: String
)
