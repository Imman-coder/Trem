package com.example.myapplication.domain.model.Serializers

import androidx.datastore.core.Serializer
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Scorecard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object ScorecardSerializer : Serializer<Scorecard> {
    override val defaultValue: Scorecard
        get() = Scorecard()




    override suspend fun readFrom(input: InputStream): Scorecard {
        return try {
             Json.decodeFromString(
                 Scorecard.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            serialization.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Scorecard, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(Scorecard.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}