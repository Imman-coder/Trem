package com.immanlv.trem.domain.repository

import com.immanlv.trem.domain.model.AppPreference
import kotlinx.coroutines.flow.Flow

interface AppDataRepository {
    suspend fun getAppPreference(): Flow<AppPreference>
    suspend fun setAppPreference( appPreference: AppPreference )
}