package com.immanlv.trem.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
@Keep
data class AttendanceDto(
    @SerializedName("aaData")
    val attendanceList: List<AttendanceSubjects>
)

@Serializable
@Keep
data class AttendanceSubjects(
    @SerializedName("0") val sl_no: Int,
    @SerializedName("1") val subject: String,
    @SerializedName("2") val type: String = "",
    @SerializedName("3") val subjectCode: String,
    @SerializedName("4") val semester: String,
    @SerializedName("5") val program: String,
    @SerializedName("6") val batch: String,
    @SerializedName("7") val credit: String = "0",
    @SerializedName("8") val section: String,
    @SerializedName("9") val rollno: String,
    @SerializedName("10") val displayName: String?,
    @SerializedName("11") val preesent: Int,
    @SerializedName("12") val total: Int,
    @SerializedName("13") val totalTopics: Int,
    @SerializedName("14") val completedTopics: Int,
    @SerializedName("15") val attendance: String,
    @SerializedName("16") val total_marks: String?,
    @SerializedName("17") val courseCoverage: String,
    @SerializedName("18") val courseHandout: String?,
    @SerializedName("19") val question: String?,
    @SerializedName("20") val conducted: Int,
    @SerializedName("21") val present: Int,
    @SerializedName("22") val totalHourAbsent: Int,
    @SerializedName("23") val percentage: String
)