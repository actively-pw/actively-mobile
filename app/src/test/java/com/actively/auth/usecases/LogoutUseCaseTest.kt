package com.actively.auth.usecases

import com.actively.http.client.AuthorizedKtorClient
import com.actively.recorder.usecase.StopRecordingUseCase
import com.actively.repository.ActivityRecordingRepository
import com.actively.repository.AuthRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

class LogoutUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val stopRecordingUseCase = mockk<StopRecordingUseCase>(relaxUnitFun = true)
    val activityRecordingRepository = mockk<ActivityRecordingRepository>(relaxUnitFun = true)
    val authRepository = mockk<AuthRepository>(relaxUnitFun = true)
    val client = mockk<AuthorizedKtorClient>(relaxed = true)
    val useCase = LogOutUseCaseImpl(
        stopRecordingUseCase = stopRecordingUseCase,
        activityRecordingRepository = activityRecordingRepository,
        authRepository = authRepository,
        client = client
    )

    test("Calls stopRecordingUseCase to stop recording") {
        useCase()
        verify(exactly = 1) { stopRecordingUseCase() }
    }

    test("Clears recording repository from all data") {
        useCase()
        coVerify(exactly = 1) { activityRecordingRepository.clear() }
    }

    test("Calls repository to logout") {
        useCase()
        coVerify(exactly = 1) { authRepository.logout() }
    }

    test("Clears client's cached tokens") {
        useCase()
        verify(exactly = 1) { client.clearCachedTokens() }
    }
})
