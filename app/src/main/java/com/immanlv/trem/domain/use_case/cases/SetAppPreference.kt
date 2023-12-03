package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.repository.AppDataRepository
import javax.inject.Inject

class SetAppPreference @Inject constructor(
    private val appDataRepository: AppDataRepository
) {

    suspend operator fun invoke(appPreference : AppPreference){
        appDataRepository.setAppPreference(appPreference = appPreference)
    }

}