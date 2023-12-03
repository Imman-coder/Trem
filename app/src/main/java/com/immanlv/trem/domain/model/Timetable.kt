package com.immanlv.trem.domain.model

import com.immanlv.trem.domain.util.DataErrorType
import kotlinx.serialization.Serializable


@Serializable
data class Timetable(
    val EventTable: List<List<Int>> = listOf(),
    val TimeList: List<Int> = listOf(),
    val EventList: List<Event> = listOf(),
    val Error: DataErrorType = DataErrorType.NoError
)

@Serializable
data class Event(
    var time_span: Int,
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


fun Timetable.getStatByCode(code:String):TimetableStat{
    var eventId = 0
    var weeklyClasses = 0
    val clWeek = mutableListOf<Int>()


    this.EventList.forEachIndexed { index,it ->
        if(it.subjects[0].subject_code == code) eventId =  index + 1
    }

    this.EventTable.forEachIndexed{id ,it ->
        for (i in it) {
            if(i == eventId){
                weeklyClasses ++
                if(id !in clWeek)
                    clWeek += id
            }
        }
    }

    return TimetableStat(
        weeklyClasses = weeklyClasses,
        classesInWeek = clWeek
    )
}

data class TimetableStat(
    val weeklyClasses:Int,
    val classesInWeek:List<Int>
)