package com.immanlv.trem.domain.model.serializers

import androidx.datastore.core.Serializer
import com.immanlv.trem.domain.model.Timetable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object TimetableSerializer: Serializer<Timetable> {
    override val defaultValue: Timetable
        get() = Timetable()


    override suspend fun readFrom(input: InputStream): Timetable {

        return try {
            Json.decodeFromString(
                Timetable.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            serialization.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Timetable, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(Timetable.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}