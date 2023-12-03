package com.immanlv.trem.network.repository

import android.util.Log
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.network.mapped.ProfileResult
import com.immanlv.trem.network.mapped.SicResult
import com.immanlv.trem.network.model.mapper.ProfileMapper
import com.immanlv.trem.network.util.NetworkService
import javax.inject.Inject

class getUserInfo
@Inject constructor(
    private val networkService: NetworkService
){
    suspend operator fun invoke():Profile{
        Log.d("TAG", "invoke: Get Sic")
        when (val res = networkService.getSic()){
            is SicResult.Failed ->{
                throw res.exception
            }
            is SicResult.Success ->{
                Log.d("TAG", "invoke: Get Profile")
                when(val res2 = networkService.getDetails(res.data)){
                    is ProfileResult.Failed -> {
                        throw res2.exception
                    }
                    is ProfileResult.Success -> {
                        Log.d("TAG", "invoke: Get Profile Success")
                        val k = ProfileMapper.mapToDomainModel(res2.data)
                        Log.d("TAG", "invoke: $k")
                        return k
                    }
                }
            }
        }

    }
}