package com.immanlv.trem.domain.model.serializers

import androidx.datastore.core.Serializer
import com.immanlv.trem.domain.model.Attendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AttendanceSerializer : Serializer<Attendance> {
    override val defaultValue: Attendance
        get() = Attendance()




    override suspend fun readFrom(input: InputStream): Attendance {
        return try {
             Json.decodeFromString(
                Attendance.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            serialization.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Attendance, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(Attendance.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}