package com.immanlv.trem.data.data_source

import androidx.datastore.core.DataStore
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.Timetable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceDao
@Inject constructor(
    private val attendanceDataStore: DataStore<Attendance>
){
    suspend fun saveAttendance(attendance: Attendance){
        attendanceDataStore.updateData {
            attendance
        }
    }
    fun getAttendance(): Flow<Attendance> = attendanceDataStore.data
}