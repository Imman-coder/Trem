package com.immanlv.trem.data.data_source

import androidx.datastore.core.DataStore
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScorecardDao
@Inject constructor(
    private val scorecardDataStore: DataStore<Scorecard>
){
    suspend fun saveScorecard(scorecard: Scorecard){
        scorecardDataStore.updateData {
            scorecard
        }
    }
    fun getScorecard(): Flow<Scorecard> = scorecardDataStore.data
}