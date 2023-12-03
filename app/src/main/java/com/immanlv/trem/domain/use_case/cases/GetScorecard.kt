package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.repository.ProfileDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetScorecard @Inject constructor(
    private val profileDataRepository: ProfileDataRepository
) {
    suspend operator fun invoke(): Flow<Scorecard> = profileDataRepository.getScorecard()
}