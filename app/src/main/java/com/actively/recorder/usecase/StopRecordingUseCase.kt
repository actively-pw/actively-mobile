package com.actively.recorder.usecase

import android.content.Context
import android.content.Intent
import com.actively.recorder.RecordActivityService

interface StopRecordingUseCase {

    suspend operator fun invoke()
}

class StopRecordingUseCaseImpl(private val context: Context) : StopRecordingUseCase {

    override suspend fun invoke() {
        val stopRecordingIntent = Intent(context, RecordActivityService::class.java).apply {
            action = RecordActivityService.STOP_ACTION
        }
        context.startForegroundService(stopRecordingIntent)
    }
}
