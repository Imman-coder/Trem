package com.immanlv.trem.data.model.serializers

import androidx.datastore.core.Serializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import java.io.InputStream
import java.io.OutputStream

object CookieSerializer: Serializer<List<Cookie>> {
    override val defaultValue: List<Cookie>
        get() = listOf()


    override suspend fun readFrom(input: InputStream): List<Cookie> {
        val gson = Gson()
        val listCookieType = object : TypeToken<List<Cookie>>() {}.type
        return gson.fromJson(
                 input.readBytes().decodeToString(),
                listCookieType
            )
    }

    override suspend fun writeTo(t: List<Cookie>, output: OutputStream) {
        withContext(Dispatchers.IO) {
            val gson = Gson()
            output.write(
                gson.toJson(t).encodeToByteArray()
            )
        }
    }
}