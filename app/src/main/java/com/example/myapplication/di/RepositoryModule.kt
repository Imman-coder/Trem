package com.example.myapplication.di

import com.example.myapplication.network.DataService
import com.example.myapplication.network.ProfileService
import com.example.myapplication.network.util.AttendenceDtoMapper
import com.example.myapplication.network.util.ProfileDtoMapper
import com.example.myapplication.network.util.ResultDtoMapper
import com.example.myapplication.network.util.TimetableDtoMapper
import com.example.myapplication.repository.DataRepository
import com.example.myapplication.repository.DataRepository_Impl
import com.example.myapplication.repository.ProfileRepository
import com.example.myapplication.repository.ProfileRepository_Impl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideProfileRepository(
        profileService:ProfileService,
        profileMapper:ProfileDtoMapper,
        attendenceMapper: AttendenceDtoMapper,
        resultMapper: ResultDtoMapper
    ): ProfileRepository{
        return ProfileRepository_Impl(
            profileService = profileService,
            profileMapper = profileMapper,
            attendanceMapper = attendenceMapper,
            resultMapper = resultMapper

        )
    }

    @Singleton
    @Provides
    fun provideDataRepository(
        dataService: DataService,
        dataMapper:TimetableDtoMapper
    ): DataRepository {
        return DataRepository_Impl(dataService,dataMapper)
    }
}