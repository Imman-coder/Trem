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

@Module
@InstallIn(SingletonComponent::class)
object AppModule{


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




}