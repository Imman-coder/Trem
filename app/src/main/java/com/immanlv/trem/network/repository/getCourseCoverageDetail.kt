package com.immanlv.trem.network.repository

import com.immanlv.trem.domain.model.CourseCoverageDetails
import com.immanlv.trem.domain.model.CoverageDetail
import com.immanlv.trem.network.mapped.CourseCoverageDetailResult
import com.immanlv.trem.network.model.mapper.CourseCoverageDetailMapper
import com.immanlv.trem.network.util.NetworkService
import javax.inject.Inject

class getCourseCoverageDetail
@Inject constructor(
    private val networkService: NetworkService
) {
    suspend operator fun invoke(
        subjectCode: String,
        sem: Int,
        branch: String,
        section: Char,
        program: String,
        studentCode: Long,
        batch: String
    ): CourseCoverageDetails {
        val k = networkService.getCourseCoverage(
            paperCode = subjectCode,
            semesterCode = sem.toString(),
            branchCode = branch,
            sectionCode = section.toString(),
            studentCode = "DGI${studentCode}",
            batchCode = "DGI_${program}_${batch}",
        )

        return when (k) {
            is CourseCoverageDetailResult.Success -> {
                CourseCoverageDetailMapper.mapToDomainModel(k.data)
            }

            else -> {
                CourseCoverageDetails(listOf())
            }
        }
    }
}