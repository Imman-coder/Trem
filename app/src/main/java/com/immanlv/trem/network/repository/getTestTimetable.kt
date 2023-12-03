package com.immanlv.trem.network.repository

import android.util.Log
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.network.mapped.TimetableResult
import com.immanlv.trem.network.model.mapper.TimetableMapper
import com.immanlv.trem.network.util.DataService
import javax.inject.Inject

class getTestTimetable
@Inject constructor(
    private val dataService: DataService
){
    suspend operator fun invoke():Timetable{
        Log.d("TAG", "invoke: getTestTimetable")
        when(val res = dataService.getTestTimetable()){
            is TimetableResult.Success ->{
                return TimetableMapper.mapToDomainModel(res.data)
            }
            is TimetableResult.Failed ->{
                throw res.exception
            }
        }
    }
}