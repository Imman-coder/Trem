package com.immanlv.trem.network.repository

import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.network.mapped.TimetableResult
import com.immanlv.trem.network.model.mapper.TimetableMapper
import com.immanlv.trem.network.util.DataService
import javax.inject.Inject

class getTimetable
@Inject constructor(
    private val dataService: DataService
){
    suspend operator fun invoke(program: String, batch: String, branch: String, section: Char):Timetable{
        when(val res = dataService.getTimetable(program, batch, branch, section)){
            is TimetableResult.Success ->{
                return TimetableMapper.mapToDomainModel(res.data)
            }
            is TimetableResult.Failed ->{
                throw res.exception
            }
        }
    }
}