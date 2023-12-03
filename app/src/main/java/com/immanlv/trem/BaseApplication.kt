package com.immanlv.trem

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {
    var isLoggedIn = mutableStateOf(false)
    var isLocalLoggedIn = mutableStateOf(false)
    var isFakeLoggedIn = mutableStateOf(false)
}