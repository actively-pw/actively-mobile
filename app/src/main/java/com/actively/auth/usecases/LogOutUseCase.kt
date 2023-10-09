package com.actively.auth.usecases

import android.content.Context
import android.content.Intent
import com.actively.http.client.AuthorizedKtorClient
import com.actively.recorder.RecordActivityService
import com.actively.repository.ActivityRecordingRepository
import com.actively.repository.AuthRepository

interface LogOutUseCase {

    suspend operator fun invoke()
}

class LogOutUseCaseImpl(
    private val activityRecordingRepository: ActivityRecordingRepository,
    private val authRepository: AuthRepository,
    private val client: AuthorizedKtorClient,
    private val context: Context,
) : LogOutUseCase {

    override suspend fun invoke() {
        val stopRecordingIntent = Intent(context, RecordActivityService::class.java).apply {
            action = RecordActivityService.STOP_ACTION
        }
        context.startForegroundService(stopRecordingIntent)
        activityRecordingRepository.clear()
        authRepository.logout()
        client.clearCachedTokens()
    }
}
