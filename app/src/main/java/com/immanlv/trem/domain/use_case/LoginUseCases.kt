package com.immanlv.trem.domain.use_case

import com.immanlv.trem.domain.use_case.cases.FakeLogin
import com.immanlv.trem.domain.use_case.cases.GetSavedCredentials
import com.immanlv.trem.domain.use_case.cases.Login
import com.immanlv.trem.domain.use_case.cases.Logout
import com.immanlv.trem.domain.use_case.cases.UpdateLoginStatus

data class LoginUseCases(
    val updateLoginStatus: UpdateLoginStatus,
    val getCredentials: GetSavedCredentials,
    val login: Login,
    val fakeLogin: FakeLogin,
    val logout: Logout,
)