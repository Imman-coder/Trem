package com.immanlv.trem.data.data_source

import android.util.Log
import androidx.datastore.core.DataStore
import com.immanlv.trem.data.model.Credentials
import com.immanlv.trem.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProfileDao
@Inject constructor(
    private val profileDataStore: DataStore<Profile>,
    private val credentialsDatastore: DataStore<Credentials>
){
    suspend fun saveProfile(profile: Profile){
        profileDataStore.updateData {
            profile
        }
    }

    fun getProfile(): Flow<Profile> {
        return profileDataStore.data
    }

    suspend fun getCredentials(): Credentials {
        return credentialsDatastore.data.first()
    }

    suspend fun saveCredentials(credentials: Credentials){
        Log.d("TAG", "saveCredentials: $credentials")
        credentialsDatastore.updateData {
            credentials
        }
    }
}