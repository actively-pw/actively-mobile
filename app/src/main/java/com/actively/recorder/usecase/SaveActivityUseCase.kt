package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository
import com.actively.synchronizer.usecases.LaunchSynchronizationUseCase

interface SaveActivityUseCase {

    suspend operator fun invoke(activityTitle: String)
}

class SaveActivityUseCaseImpl(
    private val launchSynchronizationUseCase: LaunchSynchronizationUseCase,
    private val activityRecordingRepository: ActivityRecordingRepository,
    private val stopRecordingUseCase: StopRecordingUseCase,
) : SaveActivityUseCase {

    override suspend fun invoke(activityTitle: String) {
        stopRecordingUseCase()
        activityRecordingRepository.updateRecordingActivityTitle(activityTitle)
        activityRecordingRepository.markActivityAsRecorded()
        activityRecordingRepository.setState(RecorderState.Idle)
        launchSynchronizationUseCase()
    }
}
