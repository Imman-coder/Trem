package com.example.myapplication.network.util


import com.example.myapplication.domain.model.Grade
import com.example.myapplication.domain.model.Result
import com.example.myapplication.domain.model.ResultSubject
import com.example.myapplication.domain.model.Sem
import com.example.myapplication.domain.util.DomainMapper
import com.example.myapplication.network.model.ResultDto
import kotlinx.collections.immutable.PersistentCollection
import kotlinx.collections.immutable.persistentListOf

class ResultDtoMapper : DomainMapper<ResultDto, Result> {
    override fun mapToDomainModel(model: ResultDto): Result {


        return Result(
            cgpa = model.cgpa,
            sems = model.sems.map {  Sem(
                sem = it.sem,
                sgpa = it.sgpa,
                subjects = it.subjects.map {
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

    override fun mapFromDomainModel(domainModel: Result): ResultDto {
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