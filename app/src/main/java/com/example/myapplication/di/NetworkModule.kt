package com.example.myapplication.di

import com.example.myapplication.network.DataService
import com.example.myapplication.network.ProfileService
import com.example.myapplication.repository.ConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    @Singleton
    @Provides
    fun provideProfileService(): ProfileService {
        val cookieJar: CookieJar = object : CookieJar {
            private val cookieStore = HashMap<String, List<Cookie>>()
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookieStore[url.host]
                return cookies ?: ArrayList()
            }
        }

        val client: OkHttpClient =  OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://driems.online/")
            .addConverterFactory(ConverterFactory())
            .client(client)
            .build()
            .create(ProfileService::class.java)
    }

    @Singleton
    @Provides
    fun provideDataService():DataService {
        return Retrofit.Builder()
            .baseUrl("https://imman-coder.github.io/d/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DataService::class.java)
    }
}