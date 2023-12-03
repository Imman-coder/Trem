package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.data.model.Credentials
import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.repository.AppDataRepository
import com.immanlv.trem.domain.repository.LoginDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedCredentials @Inject constructor(
    private val loginDataRepository: LoginDataRepository
) {
    suspend operator fun invoke(): Credentials = loginDataRepository.getCredentials()
}