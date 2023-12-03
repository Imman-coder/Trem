package com.immanlv.trem.data.repository

import android.util.Log
import com.immanlv.trem.BaseApplication
import com.immanlv.trem.data.data_source.ProfileDao
import com.immanlv.trem.data.model.Credentials
import com.immanlv.trem.data.model.LoginStatusE
import com.immanlv.trem.data.util.NetworkRequests
import com.immanlv.trem.di.DatastoreManager
import com.immanlv.trem.di.DatastoreModule
import com.immanlv.trem.di.util.CookieServer
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.repository.LoginDataRepository
import com.immanlv.trem.network.repository.NetworkRequestsImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import javax.inject.Inject

class LoginDataRepositoryImpl
@Inject constructor(
    private val networkRequests: NetworkRequests,
    private val cookieServer: CookieServer,
    private val profileDao: ProfileDao,
    private val datastoreManager: DatastoreManager,
    private val app: BaseApplication
) : LoginDataRepository {

    override suspend fun login(
        id: String,
        password: String,
        saveCredential: Boolean
    ): Flow<LoginStatusE> = flow {
        networkRequests.login(id, password)
            .takeWhile {
                it != LoginStatusE.Success
            }
            .collect {
                Log.d("TAG", "login: $it")
                emit(it)
            }
        emit(LoginStatusE.BindingUp)
        Log.d("TAG", "login: Login Success Binding up")
        profileDao.saveCredentials(
            Credentials(
                hasCredentials = true,
                isFakeLogin = false,
                saveCredentials = saveCredential,
                uid = id,
                pass = password
            )
        )
        app.isLoggedIn.value = true
        app.isLocalLoggedIn.value = true
        emit(LoginStatusE.Success)
    }

    override suspend fun login()  {
        val cred = profileDao.getCredentials()
        login(cred.uid, cred.pass, true)
            .takeWhile {
                it != LoginStatusE.Success
            }
            .collect {
                Log.d("TAG", "login(): $it")
            }
        app.isLocalLoggedIn.value = true
        app.isLoggedIn.value = true

    }

    override suspend fun fakeLogin(profile: Profile) {
        profileDao.saveProfile(profile)
        profileDao.saveCredentials(Credentials(isFakeLogin = true))
        app.isLocalLoggedIn.value = true
        app.isLoggedIn.value = true
    }

    override suspend fun getCredentials(): Credentials {
        return profileDao.getCredentials()
    }

    override suspend fun getFakeLoginStatus(): Boolean {
        val k = profileDao.getCredentials().isFakeLogin
        app.isLoggedIn.value = k
        return k
    }

    override suspend fun getLocalLoginStatus(): Boolean {
        val k = profileDao.getCredentials().hasCredentials
        app.isLocalLoggedIn.value = k
        return k
    }



    override suspend fun getLoginStatus(): Boolean {
        val k = getLoginStatusFromNetwork()
        app.isLoggedIn.value = k
        Log.d("TAG", "getLoginStatus: $k")
        return k
    }

    private suspend fun getLoginStatusFromNetwork(): Boolean {
        if (!profileDao.getCredentials().hasCredentials) return false
        return networkRequests.getLoginStatus()
    }

    override suspend fun logout() {
        datastoreManager.reset()
        if( !profileDao.getCredentials().saveCredentials )
            profileDao.saveCredentials(Credentials())
        else {
            profileDao.saveCredentials(
                profileDao.getCredentials().copy(
                    hasCredentials = false
                )
            )
        }

        app.isLoggedIn.value = false
        app.isLocalLoggedIn.value = false
        app.isFakeLoggedIn.value = false
    }
}