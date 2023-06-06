package com.example.myapplication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.myapplication.domain.model.AppPreferences
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Scorecard
import com.example.myapplication.domain.model.Serializers.AppPreferencesSerializer
import com.example.myapplication.domain.model.Serializers.AttendanceSerializer
import com.example.myapplication.domain.model.Serializers.CredentialsSerializer
import com.example.myapplication.domain.model.Serializers.ProfileSerializer
import com.example.myapplication.domain.model.Serializers.ScorecardSerializer
import com.example.myapplication.domain.model.Serializers.TimetableSerializer
import com.example.myapplication.domain.model.Timetable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreManager {

    private val Context.profileStore : DataStore<Profile> by dataStore("profile.json", ProfileSerializer)
    private val Context.credentialsStore: DataStore<Credentials> by dataStore("credentials.json", CredentialsSerializer)
    private val Context.preferencesStore: DataStore<AppPreferences> by dataStore("preferences.json", AppPreferencesSerializer)
    private val Context.resultStore: DataStore<Scorecard> by dataStore("result.json", ScorecardSerializer)
    private val Context.timetableStore: DataStore<Timetable> by dataStore("timetable.json", TimetableSerializer)
    private val Context.attendanceStore: DataStore<Attendance> by dataStore("attendance.json", AttendanceSerializer)

    @Singleton
    @Provides
    fun provideProfileStore(@ApplicationContext context: Context):DataStore<Profile> {
        return context.profileStore;
    }

    @Singleton
    @Provides
    fun provideCredentialsStore(@ApplicationContext context: Context):DataStore<Credentials> {
        return context.credentialsStore;
    }

    @Singleton
    @Provides
    fun provideAppPreferencesStore(@ApplicationContext context: Context):DataStore<AppPreferences> {
        return context.preferencesStore;
    }

    @Singleton
    @Provides
    fun provideResultStore(@ApplicationContext context: Context):DataStore<Scorecard> {
        return context.resultStore;
    }

    @Singleton
    @Provides
    fun provideTimetableStore(@ApplicationContext context: Context):DataStore<Timetable> {
        return context.timetableStore;
    }

    @Singleton
    @Provides
    fun provideAttendanceStore(@ApplicationContext context: Context):DataStore<Attendance> {
        return context.attendanceStore;
    }

}