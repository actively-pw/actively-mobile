package com.actively.recorder.usecase

import android.content.Context
import android.content.Intent
import com.actively.recorder.RecordActivityService
import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository
import com.actively.synchronizer.usecases.LaunchSynchronizationUseCase

interface StopRecordingUseCase {

    suspend operator fun invoke(activityTitle: String)
}

class StopRecordingUseCaseImpl(
    private val launchSynchronizationUseCase: LaunchSynchronizationUseCase,
    private val activityRecordingRepository: ActivityRecordingRepository,
    private val context: Context
) : StopRecordingUseCase {

    override suspend fun invoke(activityTitle: String) {
        val stopRecordingIntent = Intent(context, RecordActivityService::class.java).apply {
            action = RecordActivityService.STOP_ACTION
        }
        context.startForegroundService(stopRecordingIntent)
        activityRecordingRepository.updateRecordingActivityTitle(activityTitle)
        activityRecordingRepository.markActivityAsRecorded()
        activityRecordingRepository.setState(RecorderState.Idle)
        launchSynchronizationUseCase()
    }
}
