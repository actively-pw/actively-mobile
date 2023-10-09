package com.actively.auth.usecases

import com.actively.http.client.AuthorizedKtorClient
import com.actively.recorder.usecase.StopRecordingUseCase
import com.actively.repository.ActivityRecordingRepository
import com.actively.repository.AuthRepository

interface LogOutUseCase {

    suspend operator fun invoke()
}

class LogOutUseCaseImpl(
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val activityRecordingRepository: ActivityRecordingRepository,
    private val authRepository: AuthRepository,
    private val client: AuthorizedKtorClient,
) : LogOutUseCase {

    override suspend fun invoke() {
        stopRecordingUseCase()
        activityRecordingRepository.clear()
        authRepository.logout()
        client.clearCachedTokens()
    }
}
