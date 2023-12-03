package com.immanlv.trem.network.repository

import com.immanlv.trem.data.model.LoginStatusE
import com.immanlv.trem.network.mapped.HashResult
import com.immanlv.trem.network.mapped.LoginResult
import com.immanlv.trem.network.util.NetworkService
import com.immanlv.trem.network.util.encrypt
import com.immanlv.trem.network.util.exception.LoginException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class login
@Inject constructor(
    private val networkService: NetworkService
){
    suspend operator fun invoke(id: String, password: String): Flow<LoginStatusE> = flow{
        if(!getLoginStatus(networkService).invoke()){
            emit(LoginStatusE.FetchingHash)
            when(val res = networkService.getHash()){
                is HashResult.Success -> {
                    emit(LoginStatusE.LoggingIn)

                    // Encrypting id and password
                    val shaPass = encrypt.getSha512(encrypt.getSha512("$id#$password") + "#${res.data}")
                    val pass = encrypt.getMd5(encrypt.getMd5("$id#$password") + "#${res.data}")
                    emit(LoginStatusE.CheckingLoginStatus)

                    // Attempt to login
                    when(val res2 = networkService.login("DGI", res.data, pass, shaPass, id)){
                        is LoginResult.Success -> {
                            emit(LoginStatusE.Success)
                        }
                        is LoginResult.Failed -> {
                            throw res2.exception
                        }
                    }
                }
                is HashResult.Failed -> {
                    when( res.exception.type ){
                        is LoginException.Type.AlreadyLoggedIn -> {
                            emit(LoginStatusE.Success)
                        }
                        else -> {
                            throw res.exception
                        }
                    }

                }
            }
            return@flow
        } else {
            emit(LoginStatusE.Success)
        }

    }
}