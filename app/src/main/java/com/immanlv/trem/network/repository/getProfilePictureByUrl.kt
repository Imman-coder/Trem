package com.immanlv.trem.network.repository

import com.immanlv.trem.network.mapped.ImageDataResult
import com.immanlv.trem.network.util.ImageUtils
import com.immanlv.trem.network.util.NetworkService
import javax.inject.Inject

class getProfilePictureByUrl
@Inject constructor(
    private val  networkService: NetworkService
){
    suspend operator fun invoke(url:String
    ):String{
        when (val res = networkService.getProfilePicture(url)){
            is ImageDataResult.Success ->{
                return res.data
            }
            is ImageDataResult.Failed ->{
                throw res.exception
            }
        }
    }
}