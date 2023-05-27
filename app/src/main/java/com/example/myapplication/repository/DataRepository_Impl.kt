package com.example.myapplication.repository

import com.example.myapplication.domain.model.Timetable
import com.example.myapplication.network.DataService
import com.example.myapplication.network.util.TimetableDtoMapper

class DataRepository_Impl(
    private val dataService:DataService,
    private val dataDtoMapper: TimetableDtoMapper
):DataRepository {
    override suspend fun getTable(fn:String):Timetable {
        return dataDtoMapper.mapToDomainModel(dataService.getTimetable(fn))
    }
}