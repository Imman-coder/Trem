package com.immanlv.trem

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.immanlv.trem.domain.model.Timetable
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow

@HiltAndroidApp
class BaseApplication: Application() {
    var isLoggedIn = mutableStateOf(false)
    var isLocalLoggedIn = mutableStateOf(false)
    var isFakeLoggedIn = mutableStateOf(false)
}