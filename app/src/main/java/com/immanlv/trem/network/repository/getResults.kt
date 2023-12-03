package com.immanlv.trem.network.repository

import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.network.mapped.ScorecardResult
import com.immanlv.trem.network.model.mapper.ResultMapper
import com.immanlv.trem.network.util.NetworkService
import javax.inject.Inject

class getResults
@Inject constructor(
    private val networkService: NetworkService
){
    suspend operator fun invoke():Scorecard{
        when(val res = networkService.getResults()){
            is ScorecardResult.Success -> {
                return ResultMapper.mapToDomainModel(res.data)
            }
            is ScorecardResult.Failed -> {
                throw res.exception
            }
        }
    }
}