package com.immanlv.trem.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TimetableDto(
    @SerializedName("EventTable")
    val EventTable: List<List<Int>>,
    @SerializedName("TimeList")
    val TimeList: List<Int>,
    @SerializedName("EventList")
    val EventList: Map<String, EventDto>
)

@Serializable
data class EventDto(
    val time_span: Int,
    @SerializedName("subjects")
    val subjects: List<SubjectDto>,
    val class_type: Int
)

@Serializable
data class SubjectDto(
    val subject: String,
    val subject_code: String,
    val teacher: String
)
