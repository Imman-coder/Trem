package com.immanlv.trem.network.repository

import com.immanlv.trem.network.mapped.UserStaResult
import com.immanlv.trem.network.util.NetworkService
import javax.inject.Inject

class logout
@Inject constructor(
    private val networkService: NetworkService
){
    suspend operator fun invoke(){
        networkService.logout()
    }
}