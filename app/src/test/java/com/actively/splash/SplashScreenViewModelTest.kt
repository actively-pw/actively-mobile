package com.actively.splash

import app.cash.turbine.test
import com.actively.repository.AuthRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SplashScreenViewModelTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val authRepository = mockk<AuthRepository>()

    test("sets isLoggedIn to false if user is not logged in") {
        coEvery { authRepository.isUserLoggedIn() } returns false
        val viewModel = SplashScreenViewModel(authRepository)
        viewModel.isLoggedIn.test {
            awaitItem() shouldBe false
        }
    }
    test("sets isLoggedIn to false if user is logged in") {
        coEvery { authRepository.isUserLoggedIn() } returns true
        val viewModel = SplashScreenViewModel(authRepository)
        viewModel.isLoggedIn.test {
            awaitItem() shouldBe true
        }
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }
})
