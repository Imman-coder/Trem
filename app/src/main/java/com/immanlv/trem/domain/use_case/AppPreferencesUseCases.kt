package com.immanlv.trem.domain.use_case

import com.immanlv.trem.domain.use_case.cases.GetAppPreference
import com.immanlv.trem.domain.use_case.cases.SetAppPreference

data class AppPreferencesUseCases(
    val getAppPreference : GetAppPreference,
    val setAppPreference: SetAppPreference
)