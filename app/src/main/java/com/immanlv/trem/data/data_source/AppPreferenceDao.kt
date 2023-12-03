package com.immanlv.trem.data.data_source

import androidx.datastore.core.DataStore
import com.immanlv.trem.domain.model.AppPreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppPreferenceDao
@Inject constructor(
    private val appPreferenceDataStore: DataStore<AppPreference>
){
    suspend fun saveAppPreference(appPreference: AppPreference){
        appPreferenceDataStore.updateData {
            appPreference
        }
    }

    fun getAppPreference(): Flow<AppPreference> = appPreferenceDataStore.data
}