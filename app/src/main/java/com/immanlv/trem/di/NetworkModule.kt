package com.immanlv.trem.di

import androidx.datastore.core.DataStore
import com.immanlv.trem.di.util.Constants.DOMAIN_BASE_URL
import com.immanlv.trem.di.util.Constants.GITHUB_BASE_URL
import com.immanlv.trem.di.util.CookieServer
import com.immanlv.trem.network.util.ConverterFactory
import com.immanlv.trem.network.util.DataService
import com.immanlv.trem.network.util.NetworkService
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
object NetworkModule {


    @Provides
    @Singleton
    fun provideCookieServer(
        cookieDataStore: DataStore< List<Cookie>>
    ): CookieServer {
        return CookieServer(cookieDataStore)
    }



    @Provides
    @Singleton
    fun provideTimetableNetworkService(): DataService {
        return Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(ConverterFactory())
//            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DataService::class.java)
    }

    @Singleton
    @Provides
    fun provideProfileNetworkService(
        cookieServer: CookieServer
    ): NetworkService {

        val cookieJar: CookieJar = object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                 cookieServer.setCookie(cookies)
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieServer.getCookie()
            }
        }

        val client: OkHttpClient =  OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
        return Retrofit.Builder()
            .baseUrl(DOMAIN_BASE_URL)
            .addConverterFactory(ConverterFactory())
            .client(client)
            .build()
            .create(NetworkService::class.java)
    }


}