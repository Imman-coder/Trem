package com.immanlv.trem.network.mapped



sealed class ImageDataResult {
    data class Success(val data:String): ImageDataResult()
    data class Failed(val exception: Exception): ImageDataResult()
}