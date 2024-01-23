package com.actively.recorder.usecase

import android.content.Context
import android.content.Intent
import com.actively.recorder.RecordActivityService

/**
 * Usecase that sends stop recording intent to [RecordActivityService]
 */
interface StopRecordingUseCase {

    operator fun invoke()
}

class StopRecordingUseCaseImpl(private val context: Context) : StopRecordingUseCase {

    override fun invoke() {
        val stopRecordingIntent = Intent(context, RecordActivityService::class.java).apply {
            action = RecordActivityService.STOP_ACTION
        }
        context.startService(stopRecordingIntent)
    }
}
