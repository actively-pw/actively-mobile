package com.actively.auth.usecases

import com.actively.repository.AuthRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk

class LogoutUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val authRepository = mockk<AuthRepository>(relaxUnitFun = true)
    val useCase = LogOutUseCaseImpl(authRepository)

    test("Calls repository to logout") {
        useCase()
        coVerify(exactly = 1) { authRepository.logout() }
    }
})
