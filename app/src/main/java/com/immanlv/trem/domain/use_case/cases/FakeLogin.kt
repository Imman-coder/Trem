package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.repository.LoginDataRepository
import com.immanlv.trem.network.util.exception.LoginException
import javax.inject.Inject
import kotlin.jvm.Throws

class FakeLogin
    @Inject
    constructor(
        private val loginDataRepository: LoginDataRepository
) {

    @Throws(LoginException::class)
    suspend operator fun invoke(profile: Profile){
        loginDataRepository.fakeLogin(profile = profile)
    }

}