package com.actively.recorder.usecase

import android.content.Context
import android.content.Intent
import com.actively.recorder.RecordActivityService

interface PauseRecordingUseCase {

    operator fun invoke()
}

class PauseRecordingUseCaseImpl(private val context: Context) : PauseRecordingUseCase {

    override fun invoke() {
        val pauseRecordingIntent = Intent(context, RecordActivityService::class.java).apply {
            action = RecordActivityService.PAUSE_ACTION
        }
        context.startForegroundService(pauseRecordingIntent)
    }
}
