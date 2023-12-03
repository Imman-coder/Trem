package com.immanlv.trem.network.model.mapper

import com.immanlv.trem.domain.model.ClassStatus
import com.immanlv.trem.domain.model.CourseCoverageDetails
import com.immanlv.trem.domain.model.CoverageDetail
import com.immanlv.trem.network.model.CourseCoverageDetailsDto
import com.immanlv.trem.network.util.DomainMapper


object CourseCoverageDetailMapper : DomainMapper<CourseCoverageDetailsDto, CourseCoverageDetails> {
    override fun mapToDomainModel(model: CourseCoverageDetailsDto):CourseCoverageDetails  {
        val m = mutableListOf<CoverageDetail>()
        model.list.forEach {
            m += CoverageDetail(
                sl = it.sl,
                date = it.date,
                time = it.time,
                topic = it.topic,
                roomNo = it.roomNo,
                status = getClassStatus(it.status),
            )
        }
        return CourseCoverageDetails(m)
    }

    override fun mapFromDomainModel(domainModel: CourseCoverageDetails): CourseCoverageDetailsDto {
        TODO("Not yet implemented")
    }
}

fun getClassStatus(s: String):ClassStatus{
    if(s.contains("res")) return ClassStatus.Present
    return ClassStatus.Absent
}