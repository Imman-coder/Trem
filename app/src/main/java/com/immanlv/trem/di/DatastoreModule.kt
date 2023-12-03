package com.immanlv.trem.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.immanlv.trem.data.model.Credentials
import com.immanlv.trem.data.model.serializers.CookieSerializer
import com.immanlv.trem.data.model.serializers.CredentialsSerializer
import com.immanlv.trem.di.DatastoreModule.attendanceStore
import com.immanlv.trem.di.DatastoreModule.cookieStore
import com.immanlv.trem.di.DatastoreModule.credentialsStore
import com.immanlv.trem.di.DatastoreModule.profileStore
import com.immanlv.trem.di.DatastoreModule.scorecardStore
import com.immanlv.trem.di.DatastoreModule.timetableStore
import com.immanlv.trem.di.util.Constants.APP_PREFERENCE_STORE_FILE_NAME
import com.immanlv.trem.di.util.Constants.ATTENDANCE_STORE_FILE_NAME
import com.immanlv.trem.di.util.Constants.COOKIE_STORE_FILE_NAME
import com.immanlv.trem.di.util.Constants.CREDENTIALS_STORE_FILE_NAME
import com.immanlv.trem.di.util.Constants.PROFILE_STORE_FILE_NAME
import com.immanlv.trem.di.util.Constants.SCORECARD_STORE_FILE_NAME
import com.immanlv.trem.di.util.Constants.TIMETABLE_STORE_FILE_NAME
import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.model.serializers.AppPreferenceSerializer
import com.immanlv.trem.domain.model.serializers.AttendanceSerializer
import com.immanlv.trem.domain.model.serializers.ProfileSerializer
import com.immanlv.trem.domain.model.serializers.ScorecardSerializer
import com.immanlv.trem.domain.model.serializers.TimetableSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cookie
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {
    val Context.timetableStore: DataStore<Timetable> by dataStore(
        TIMETABLE_STORE_FILE_NAME,
        TimetableSerializer
    )
    val Context.profileStore: DataStore<Profile> by dataStore(
        PROFILE_STORE_FILE_NAME,
        ProfileSerializer
    )
    val Context.attendanceStore: DataStore<Attendance> by dataStore(
        ATTENDANCE_STORE_FILE_NAME,
        AttendanceSerializer
    )
    val Context.scorecardStore: DataStore<Scorecard> by dataStore(
        SCORECARD_STORE_FILE_NAME,
        ScorecardSerializer
    )

    val Context.credentialsStore: DataStore<Credentials> by dataStore(
        CREDENTIALS_STORE_FILE_NAME,
        CredentialsSerializer
    )
    private val Context.appPreferenceStore: DataStore<AppPreference> by dataStore(
        APP_PREFERENCE_STORE_FILE_NAME,
        AppPreferenceSerializer
    )
    val Context.cookieStore: DataStore<List<Cookie>> by dataStore(
        COOKIE_STORE_FILE_NAME,
        CookieSerializer
    )


    @Provides
    @Singleton
    fun provideTimetableDatastore(@ApplicationContext context: Context): DataStore<Timetable> {
        return context.timetableStore
    }

    @Provides
    @Singleton
    fun provideAttendanceDatastore(@ApplicationContext context: Context): DataStore<Attendance> {
        return context.attendanceStore
    }

    @Provides
    @Singleton
    fun provideCookieDatastore(@ApplicationContext context: Context): DataStore<List<Cookie>> {
        return context.cookieStore
    }

    @Provides
    @Singleton
    fun provideAppPreferenceDatastore(@ApplicationContext context: Context): DataStore<AppPreference> {
        return context.appPreferenceStore
    }

    @Provides
    @Singleton
    fun provideCredentialsDatastore(@ApplicationContext context: Context): DataStore<Credentials> {
        return context.credentialsStore
    }

    @Provides
    @Singleton
    fun provideProfileDatastore(@ApplicationContext context: Context): DataStore<Profile> {
        return context.profileStore
    }

    @Provides
    @Singleton
    fun provideScorecardDatastore(@ApplicationContext context: Context): DataStore<Scorecard> {
        return context.scorecardStore
    }


    @Provides
    @Singleton
    fun provideDatastoreManager(@ApplicationContext context: Context): DatastoreManager {
        return DatastoreManager(context)
    }

}

class DatastoreManager(val context: Context) {
    suspend fun reset() {
        context.timetableStore.updateData {
            Timetable()
        }
        context.attendanceStore.updateData {
            Attendance()
        }
        context.cookieStore.updateData {
            listOf()
        }
        context.profileStore.updateData {
            Profile()
        }
        context.scorecardStore.updateData {
            Scorecard()
        }
    }
}