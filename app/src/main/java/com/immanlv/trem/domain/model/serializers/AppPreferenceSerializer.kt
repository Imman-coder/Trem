package com.immanlv.trem.domain.model.serializers

import androidx.datastore.core.Serializer
import com.immanlv.trem.domain.model.AppPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppPreferenceSerializer: Serializer<AppPreference> {
    override val defaultValue: AppPreference
        get() = AppPreference()


    override suspend fun readFrom(input: InputStream): AppPreference {

        return try {
            Json.decodeFromString(
                AppPreference.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            serialization.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppPreference, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(AppPreference.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}