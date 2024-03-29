package com.immanlv.trem.network.model.mapper

import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.model.Subject
import com.immanlv.trem.network.model.EventDto
import com.immanlv.trem.network.model.SubjectDto
import com.immanlv.trem.network.model.TimetableDto
import com.immanlv.trem.network.util.DomainMapper

object TimetableMapper:DomainMapper<TimetableDto,Timetable> {
    override fun mapToDomainModel(model: TimetableDto): Timetable {
        return Timetable(
            eventTable = model.EventTable,
            timeList = model.TimeList,
            eventList = model.EventList.map { event ->
                Event(
                    event.value.time_span,
                    event.value.subjects.map {
                        Subject(it.subject,
                            it.subject_code,
                            it.teacher)
                    },
                    getClassType(event.value.class_type)
                )
            }
        )
    }

    override fun mapFromDomainModel(domainModel: Timetable): TimetableDto {
        val m = mutableMapOf<String,EventDto>()
        domainModel.eventList.forEachIndexed{ index, event ->
            m["${index + 1}"] =
            EventDto(
                event.timeSpan,
                event.subjects.map {
                    SubjectDto(
                        it.subject,
                        it.subjectCode,
                        it.teacher
                    )
                },
                event.classType.ordinal
            )
        }
        return TimetableDto(
            EventTable = domainModel.eventTable,
            TimeList = domainModel.timeList,
            EventList = m
        )
    }
}

fun getClassType(v:Int): ClassType {
    return when(v){
        0 -> ClassType.Theory
        1 -> ClassType.Lab
        else -> ClassType.Notice
    }
}