package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository

/**
 * Usecase to discard recorded activity. Discarded activity will not be send to backend and will be permanently lost.
 * Sends Intent to [RecordActivityService].
 */
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
