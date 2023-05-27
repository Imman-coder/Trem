package com.example.myapplication.network.util

import com.example.myapplication.domain.model.ClassType
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.Subject
import com.example.myapplication.domain.model.Timetable
import com.example.myapplication.domain.util.DomainMapper
import com.example.myapplication.network.model.TimetableDto

class TimetableDtoMapper:DomainMapper<TimetableDto,Timetable> {
    override fun mapToDomainModel(model: TimetableDto): Timetable {
        return Timetable(
            EventTable = model.EventTable,
            TimeList = model.TimeList,
            EventList = model.EventList.map {
                Event(
                    it.value.time_span,
                    it.value.subjects.map {
                                          Subject(it.subject,
                                          it.subject_code,
                                          it.teacher)
                    },
                    getClassType(it.value.class_type)
                )
            }
        )
    }

    override fun mapFromDomainModel(domainModel: Timetable): TimetableDto {
        TODO("Not yet implemented")
    }
}

fun getClassType(v:Int):ClassType{
    return when(v){
        0 -> ClassType.Theory
        1 -> ClassType.Lab
        else -> ClassType.Notice
    }
}