package com.immanlv.trem.network.model.mapper

import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.domain.model.SubjectType
import com.immanlv.trem.network.model.AttendanceDto
import com.immanlv.trem.network.util.DomainMapper


object AttendanceMapper : DomainMapper<AttendanceDto, Attendance> {
    override fun mapToDomainModel(model: AttendanceDto): Attendance {
        return Attendance(
            total = .8f,
            subs = model.attendanceList.map { sub ->

                AttendanceSubject(
                    name = sub.subject,
                    absent = sub.conducted - sub.present,
                    present = sub.present,
                    conducted = sub.conducted,
                    code = getCode(sub.subject),
                    credit = sub.credit.toInt(),
                    courseCoverageLink = sub.courseCoverage,
                    type = getClassType(sub.type),
                )

            }
        )
    }

    override fun mapFromDomainModel(domainModel: Attendance): AttendanceDto {
        TODO("Not yet implemented")
    }
}

fun getCode(name: String): String {
    val words = name.split("(")
    return words[1].dropLast(1);
}

fun getClassType(name: String): SubjectType {
    return if (name.contains("(lab)|(LAB)")) SubjectType.LAB else SubjectType.THEORY
}