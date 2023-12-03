package com.immanlv.trem.network.repository

import com.immanlv.trem.network.mapped.UserStaResult
import com.immanlv.trem.network.util.NetworkService
import javax.inject.Inject

class getLoginStatus
@Inject constructor(
    private val networkService: NetworkService
){
    suspend operator fun invoke():Boolean{
        return when(networkService.getUserSta()){
            is UserStaResult.Success -> {
                true
            }
            else ->{
                false
            }
        }
    }
}