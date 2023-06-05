package com.example.myapplication.domain.model.Serializers

import androidx.datastore.core.Serializer
import com.example.myapplication.domain.model.AppPreferences
import com.example.myapplication.domain.model.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppPreferencesSerializer:Serializer<AppPreferences> {
    override val defaultValue: AppPreferences
        get() = AppPreferences(false,false,false)

    override suspend fun readFrom(input: InputStream): AppPreferences {
        return try {
            Json.decodeFromString(
                AppPreferences.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            serialization.printStackTrace()
            defaultValue
        }

    }

    override suspend fun writeTo(t: AppPreferences, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(AppPreferences.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}