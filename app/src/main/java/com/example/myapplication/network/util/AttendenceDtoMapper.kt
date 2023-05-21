package com.example.myapplication.network.util

import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.AttendanceSubject
import com.example.myapplication.domain.util.DomainMapper
import com.example.myapplication.network.model.AttendanceDto

class AttendenceDtoMapper:DomainMapper<AttendanceDto, Attendance> {
    override fun mapToDomainModel(model: AttendanceDto): Attendance {
        return Attendance(
            total = .8f,
            subs = model.AttendnceList.map { sub ->

                    AttendanceSubject(
                        name = sub.subject,
                        absent = sub.conducted - sub.present,
                        present = sub.present,
                        conducted = sub.conducted,
                        code = getCode(sub.subject)
                    )

    }
        )
    }

    override fun mapFromDomainModel(domainModel: Attendance): AttendanceDto {
        TODO("Not yet implemented")
    }
}
fun getCode(name: String):String{
    val words = name.split("(")
    return words[1].dropLast(1);
}