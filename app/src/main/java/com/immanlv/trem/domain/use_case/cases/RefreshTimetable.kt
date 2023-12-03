package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.domain.repository.ProfileDataRepository
import javax.inject.Inject

class RefreshTimetable @Inject constructor(
    private val profileDataRepository: ProfileDataRepository
) {

    suspend operator fun invoke(){
        profileDataRepository.refreshTimetable()
    }

}