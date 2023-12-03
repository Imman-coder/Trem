package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.domain.repository.LoginDataRepository
import javax.inject.Inject

class Logout @Inject constructor(
    private val loginDataRepository: LoginDataRepository
) {

    suspend operator fun invoke()  {
        loginDataRepository.logout()
    }

}