package com.immanlv.trem.data.model.serializers

import androidx.datastore.core.Serializer
import com.immanlv.trem.data.model.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object CredentialsSerializer : Serializer<Credentials> {
    override val defaultValue: Credentials
        get() = Credentials()




    override suspend fun readFrom(input: InputStream): Credentials {
        return try {
            Json.decodeFromString(
                Credentials.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            serialization.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Credentials, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(Credentials.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}