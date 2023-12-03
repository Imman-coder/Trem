package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.data.model.LoginStatusE
import com.immanlv.trem.domain.repository.LoginDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Login @Inject constructor(
    private val loginDataRepository: LoginDataRepository
) {

    suspend operator fun invoke(username:String, password:String, saveCredentials:Boolean): Flow<LoginStatusE> = flow{
        loginDataRepository.login(username,password,saveCredentials).collect {
            emit(it)
        }
    }

}