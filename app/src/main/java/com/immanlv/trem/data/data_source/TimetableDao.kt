package com.immanlv.trem.data.data_source

import androidx.datastore.core.DataStore
import com.immanlv.trem.domain.model.Timetable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimetableDao
@Inject constructor(
    private val timetableDataStore: DataStore<Timetable>
){
    suspend fun saveTimetable(timetable: Timetable){
        timetableDataStore.updateData {
            timetable
        }
    }
    fun getTimetable(): Flow<Timetable> = timetableDataStore.data
}