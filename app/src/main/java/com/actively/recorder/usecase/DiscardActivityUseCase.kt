package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository

interface DiscardActivityUseCase {

    suspend operator fun invoke()
}

class DiscardActivityUseCaseImpl(
    private val activityRecordingRepository: ActivityRecordingRepository
) : DiscardActivityUseCase {

    override suspend operator fun invoke() {
        activityRecordingRepository.removeRecordingActivity()
        activityRecordingRepository.setState(RecorderState.Idle)
    }
}
