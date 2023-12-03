package com.immanlv.trem.domain.repository

import com.immanlv.trem.data.model.Credentials
import com.immanlv.trem.data.model.LoginStatusE
import com.immanlv.trem.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface LoginDataRepository {
    suspend fun login(id:String,password:String,saveCredential:Boolean): Flow<LoginStatusE>

    suspend fun fakeLogin(profile: Profile)

    suspend fun getCredentials():Credentials

    suspend fun getLoginStatus():Boolean

    suspend fun getLocalLoginStatus():Boolean

    suspend fun getFakeLoginStatus():Boolean

    suspend fun login()

    suspend fun logout()
}