package com.example.myapplication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.myapplication.BaseApplication
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Serializers.CredentialsSerializer
import com.example.myapplication.domain.model.Serializers.ProfileSerializer
import com.example.myapplication.network.util.AttendenceDtoMapper
import com.example.myapplication.network.util.ProfileDtoMapper
import com.example.myapplication.network.util.ResultDtoMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.profileStore by dataStore("profile.json", ProfileSerializer)
val Context.credentialsStore by dataStore("credentials.json", CredentialsSerializer)

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
    fun provideProfileStore(@ApplicationContext context: Context):DataStore<Profile> {
        return context.profileStore;
    }

    @Singleton
    @Provides
    fun provideCredentials(@ApplicationContext context: Context):DataStore<Credentials> {
        return context.credentialsStore;
    }


}