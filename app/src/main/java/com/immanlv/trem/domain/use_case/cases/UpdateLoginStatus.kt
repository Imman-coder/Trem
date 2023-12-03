package com.immanlv.trem.domain.use_case.cases

import android.util.Log
import com.immanlv.trem.BaseApplication
import com.immanlv.trem.domain.repository.LoginDataRepository
import javax.inject.Inject

class UpdateLoginStatus @Inject constructor(
    private val loginDataRepository: LoginDataRepository,
    private val app:BaseApplication
) {
    suspend operator fun invoke(){
        app.isLocalLoggedIn.value = loginDataRepository.getLocalLoginStatus()
    }
}