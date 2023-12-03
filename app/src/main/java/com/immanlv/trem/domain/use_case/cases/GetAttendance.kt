package com.immanlv.trem.domain.use_case.cases

import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.repository.ProfileDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendance @Inject constructor(
    private val profileDataRepository: ProfileDataRepository
) {

    suspend operator fun invoke(): Flow<Attendance> = profileDataRepository.getAttendance()

}