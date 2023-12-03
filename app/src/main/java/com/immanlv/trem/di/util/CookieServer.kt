package com.immanlv.trem.di.util

import androidx.compose.runtime.mutableStateMapOf
import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.Cookie
import javax.inject.Inject

class CookieServer @Inject constructor(
    private val cookieDataStore: DataStore<List<Cookie>>
) {
    private var _cookie = listOf<Cookie>()

    init {
        cookieDataStore.data.onEach {
            _cookie = it
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun clearCookie() {
        CoroutineScope(Dispatchers.IO).launch {
            _cookie = listOf()
            cookieDataStore.updateData {
                listOf()
            }
        }
    }

    fun getCookie(): List<Cookie> {
        return _cookie
    }

    fun setCookie(value: List<Cookie>) {
        val names = mutableStateMapOf<String, Cookie>()
        val validCookies = mutableListOf<Cookie>()

        value
            .asReversed()
            .forEach {
                if (it.name !in names) {
                    names[it.name] = it
                } else if (names[it.name]!!.expiresAt < it.expiresAt) {
                    names[it.name] = it
                }
            }
        names.forEach {
            validCookies.add(it.value)
        }
        CoroutineScope(Dispatchers.IO).launch {
            cookieDataStore.updateData {
                validCookies
            }
        }
    }


}