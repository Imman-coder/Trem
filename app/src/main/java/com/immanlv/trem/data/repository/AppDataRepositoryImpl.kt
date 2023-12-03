package com.immanlv.trem.data.repository;

import com.immanlv.trem.data.data_source.AppPreferenceDao
import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.repository.AppDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppDataRepositoryImpl
    @Inject
    constructor(
        private val appPreferenceDao: AppPreferenceDao
    )
    : AppDataRepository {
    override suspend fun getAppPreference(): Flow<AppPreference> {
        return appPreferenceDao.getAppPreference()
    }

    override suspend fun setAppPreference(appPreference: AppPreference) {
        appPreferenceDao.saveAppPreference(appPreference)
    }
}
