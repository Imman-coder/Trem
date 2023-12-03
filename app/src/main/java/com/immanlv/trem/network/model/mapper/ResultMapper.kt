package com.immanlv.trem.network.model.mapper

import com.immanlv.trem.domain.model.Grade
import com.immanlv.trem.domain.model.ResultSubject
import com.immanlv.trem.domain.model.Sem
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.network.model.ScorecardDto
import com.immanlv.trem.network.util.DomainMapper

object ResultMapper:DomainMapper<ScorecardDto,Scorecard> {
    override fun mapToDomainModel(model: ScorecardDto): Scorecard {


        return Scorecard(
            cgpa = model.cgpa,
            sems = model.sems.map { it2 ->
                Sem(
                    sem = it2.sem,
                    sgpa = it2.sgpa,
                    subjects = it2.subjects.map {
                        ResultSubject(
                            name = it.name,
                            code = it.code,
                            credit = it.credit,
                            grade = gradeMapper(it.grade)
                        )
                    }

                ) }
        )
    }

    override fun mapFromDomainModel(domainModel: Scorecard): ScorecardDto {
        TODO("Not yet implemented")
    }
}


fun gradeMapper(s:Char): Grade {
    return when(s.lowercaseChar()){
        'a'-> Grade.A
        'b'->Grade.B
        'c'->Grade.C
        'd'->Grade.D
        'e'->Grade.E
        'o'->Grade.O
        else -> Grade.F
    }
}