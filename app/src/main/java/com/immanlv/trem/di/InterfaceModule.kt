package com.immanlv.trem.di

import androidx.datastore.core.DataStore
import com.immanlv.trem.BaseApplication
import com.immanlv.trem.data.data_source.AppPreferenceDao
import com.immanlv.trem.data.data_source.AttendanceDao
import com.immanlv.trem.data.data_source.ProfileDao
import com.immanlv.trem.data.data_source.ScorecardDao
import com.immanlv.trem.data.data_source.TimetableDao
import com.immanlv.trem.data.model.Credentials
import com.immanlv.trem.data.repository.AppDataRepositoryImpl
import com.immanlv.trem.data.repository.LoginDataRepositoryImpl
import com.immanlv.trem.data.repository.ProfileDataRepositoryImpl
import com.immanlv.trem.data.util.NetworkRequests
import com.immanlv.trem.di.util.CookieServer
import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.repository.AppDataRepository
import com.immanlv.trem.domain.repository.LoginDataRepository
import com.immanlv.trem.domain.repository.ProfileDataRepository
import com.immanlv.trem.domain.use_case.AppPreferencesUseCases
import com.immanlv.trem.domain.use_case.LoginUseCases
import com.immanlv.trem.domain.use_case.ProfileUseCases
import com.immanlv.trem.domain.use_case.cases.FakeLogin
import com.immanlv.trem.domain.use_case.cases.GetAppPreference
import com.immanlv.trem.domain.use_case.cases.GetAttendance
import com.immanlv.trem.domain.use_case.cases.GetProfile
import com.immanlv.trem.domain.use_case.cases.GetSavedCredentials
import com.immanlv.trem.domain.use_case.cases.GetScorecard
import com.immanlv.trem.domain.use_case.cases.GetTimetable
import com.immanlv.trem.domain.use_case.cases.Login
import com.immanlv.trem.domain.use_case.cases.Logout
import com.immanlv.trem.domain.use_case.cases.RefreshAttendance
import com.immanlv.trem.domain.use_case.cases.RefreshLastUpdated
import com.immanlv.trem.domain.use_case.cases.RefreshProfile
import com.immanlv.trem.domain.use_case.cases.RefreshScorecard
import com.immanlv.trem.domain.use_case.cases.RefreshTimetable
import com.immanlv.trem.domain.use_case.cases.SetAppPreference
import com.immanlv.trem.domain.use_case.cases.UpdateLoginStatus
import com.immanlv.trem.network.repository.NetworkRequestsImpl
import com.immanlv.trem.network.repository.getAttendance
import com.immanlv.trem.network.repository.getCourseCoverageDetail
import com.immanlv.trem.network.repository.getLoginStatus
import com.immanlv.trem.network.repository.getProfilePicture
import com.immanlv.trem.network.repository.getProfilePictureByUrl
import com.immanlv.trem.network.repository.getResults
import com.immanlv.trem.network.repository.getTestTimetable
import com.immanlv.trem.network.repository.getTimetable
import com.immanlv.trem.network.repository.getUserInfo
import com.immanlv.trem.network.repository.login
import com.immanlv.trem.network.repository.logout
import com.immanlv.trem.network.util.DataService
import com.immanlv.trem.network.util.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterfaceModule {

    @Provides
    @Singleton
    fun provideAppPreferenceDao(preference: DataStore<AppPreference>): AppPreferenceDao =
        AppPreferenceDao(preference)

    @Provides
    @Singleton
    fun provideAttendanceDao(preference: DataStore<Attendance>): AttendanceDao =
        AttendanceDao(preference)

    @Provides
    @Singleton
    fun provideProfileDao(
        preference: DataStore<Profile>,
        credentialPreference: DataStore<Credentials>
    ): ProfileDao = ProfileDao(preference, credentialPreference)

    @Provides
    @Singleton
    fun provideScorecardDao(preference: DataStore<Scorecard>): ScorecardDao =
        ScorecardDao(preference)

    @Provides
    @Singleton
    fun provideTimetableDao(preference: DataStore<Timetable>): TimetableDao =
        TimetableDao(preference)


    @Provides
    @Singleton
    fun provideNetworkRequests(
        networkService: NetworkService,
        dataService: DataService,
    ): NetworkRequests = NetworkRequestsImpl(
        _login =  login(networkService),
        _getLoginStatus =  getLoginStatus(networkService),
        _getUserInfo =  getUserInfo(networkService),
        _getProfilePicture =  getProfilePicture(networkService),
        _getProfilePictureByUrl = getProfilePictureByUrl(networkService ),
        _getAttendance =  getAttendance(networkService),
        _getResults =  getResults(networkService),
        _getTimetable =  getTimetable(dataService),
        _getTestTimetable = getTestTimetable(dataService),
        _logout = logout(networkService),
        _getCourseCoverageDetail = getCourseCoverageDetail(networkService)
    )


    @Provides
    @Singleton
    fun provideAppDataRepository(appPreference: AppPreferenceDao): AppDataRepository =
        AppDataRepositoryImpl(appPreference)

    @Provides
    @Singleton
    fun provideLoginDataRepository(
        app: BaseApplication,
        networkRequests: NetworkRequests,
        profileDao: ProfileDao,
        datastoreManager: DatastoreManager
    ): LoginDataRepository =
        LoginDataRepositoryImpl(
            networkRequests = networkRequests,
            profileDao = profileDao,
            app = app,
            datastoreManager = datastoreManager
        )

    @Provides
    @Singleton
    fun provideProfileDataRepository(
        profileDao: ProfileDao,
        attendanceDao: AttendanceDao,
        timetableDao: TimetableDao,
        scorecardDao: ScorecardDao,
        loginDataRepository: LoginDataRepository,
        networkRequests: NetworkRequests,
        appDataRepository: AppDataRepository
    ): ProfileDataRepository =
        ProfileDataRepositoryImpl(
            profileDao = profileDao,
            attendanceDao = attendanceDao,
            timetableDao = timetableDao,
            scorecardDao = scorecardDao,
            loginDataRepository = loginDataRepository,
            networkRequests = networkRequests,
            appDataRepository = appDataRepository,
        )


    @Provides
    @Singleton
    fun provideAppPreferenceUseCase(appDataRepository: AppDataRepository): AppPreferencesUseCases =
        AppPreferencesUseCases(
            getAppPreference = GetAppPreference(appDataRepository),
            setAppPreference = SetAppPreference(appDataRepository)
        )

    @Provides
    @Singleton
    fun provideLoginUseCase(loginDataRepository: LoginDataRepository,datastoreManager: DatastoreManager,app: BaseApplication): LoginUseCases =
        LoginUseCases(
            updateLoginStatus = UpdateLoginStatus(loginDataRepository,app),
            login = Login(loginDataRepository,datastoreManager),
            fakeLogin = FakeLogin(loginDataRepository),
            logout = Logout(loginDataRepository),
            getCredentials = GetSavedCredentials(loginDataRepository)
        )

    @Provides
    @Singleton
    fun provideProfileUseCase(profileDataRepository: ProfileDataRepository): ProfileUseCases =
        ProfileUseCases(
            getProfile = GetProfile(profileDataRepository),
            getAttendance = GetAttendance(profileDataRepository),
            getTimetable = GetTimetable(profileDataRepository),
            refreshProfile = RefreshProfile(profileDataRepository),
            refreshAttendance = RefreshAttendance(profileDataRepository),
            refreshTimetable = RefreshTimetable(profileDataRepository),
            getScorecard = GetScorecard(profileDataRepository),
            refreshScorecard = RefreshScorecard(profileDataRepository),
            refreshLastUpdated = RefreshLastUpdated(profileDataRepository),
        )

}