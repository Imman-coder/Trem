package com.example.myapplication.repository

import com.example.myapplication.domain.model.Timetable

interface DataRepository {
    suspend fun getTable(fn:String="testTable.json"):Timetable
}