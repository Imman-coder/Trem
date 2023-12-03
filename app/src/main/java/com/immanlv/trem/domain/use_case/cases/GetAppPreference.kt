package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.repository.AppDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppPreference @Inject constructor(
    private val appDataRepository: AppDataRepository
) {
    suspend operator fun invoke(): Flow<AppPreference> = appDataRepository.getAppPreference()
}