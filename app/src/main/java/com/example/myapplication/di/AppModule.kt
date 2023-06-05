package com.example.myapplication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.myapplication.BaseApplication
import com.example.myapplication.domain.model.AppPreferences
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Serializers.AppPreferencesSerializer
import com.example.myapplication.domain.model.Serializers.CredentialsSerializer
import com.example.myapplication.domain.model.Serializers.ProfileSerializer
import com.example.myapplication.network.util.AttendenceDtoMapper
import com.example.myapplication.network.util.ProfileDtoMapper
import com.example.myapplication.network.util.ResultDtoMapper
import com.example.myapplication.network.util.TimetableDtoMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.prefs.Preferences
import javax.inject.Singleton

//val Context.profileStore by dataStore("profile.json", ProfileSerializer)
//val Context.credentialsStore by dataStore("credentials.json", CredentialsSerializer)

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    private val Context.profileStore :DataStore<Profile> by dataStore("profile.json", ProfileSerializer)
    private val Context.credentialsStore: DataStore<Credentials> by dataStore("credentials.json", CredentialsSerializer)
    private val Context.preferencesStore: DataStore<AppPreferences> by dataStore("preferences.json", AppPreferencesSerializer)


    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app:Context):BaseApplication{

        return app as BaseApplication
    }

    @Singleton
    @Provides
    fun provideProfileMapper(): ProfileDtoMapper {
        return ProfileDtoMapper()
    }

    @Singleton
    @Provides
    fun provideAttendenceMapper(): AttendenceDtoMapper {
        return AttendenceDtoMapper()
    }

    @Singleton
    @Provides
    fun provideResultMapper(): ResultDtoMapper {
        return ResultDtoMapper()
    }

    @Singleton
    @Provides
    fun provideTimetableMapper(): TimetableDtoMapper {
        return TimetableDtoMapper()
    }

    @Singleton
    @Provides
    fun provideProfileStore(@ApplicationContext context: Context):DataStore<Profile> {
        return context.profileStore;
    }

    @Singleton
    @Provides
    fun provideCredentials(@ApplicationContext context: Context):DataStore<Credentials> {
        return context.credentialsStore;
    }

    @Singleton
    @Provides
    fun provideAppPreferences(@ApplicationContext context: Context):DataStore<AppPreferences> {
        return context.preferencesStore;
    }



}