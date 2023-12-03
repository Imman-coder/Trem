package com.immanlv.trem.network.repository

import com.immanlv.trem.network.mapped.ImageDataResult
import com.immanlv.trem.network.util.ImageUtils
import com.immanlv.trem.network.util.NetworkService
import javax.inject.Inject

class getProfilePicture
@Inject constructor(
    private val  networkService: NetworkService
){
    suspend operator fun invoke(
        program: String,
        batch: String,
        branch: String,
        sicno: Long
    ):String{
        when (val res = networkService.getProfilePicture(program,batch,branch,sicno)){
            is ImageDataResult.Success ->{
                return res.data
            }
            is ImageDataResult.Failed ->{
                throw res.exception
            }
        }
    }
}