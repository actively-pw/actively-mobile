package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository

/**
 * Sets state of the recorder.
 */
interface SetRecorderStateUseCase {

    suspend operator fun invoke(state: RecorderState)
}

class SetRecorderStateUseCaseImpl(
    private val activityRecordingRepository: ActivityRecordingRepository
) : SetRecorderStateUseCase {

    override suspend fun invoke(state: RecorderState) {
        activityRecordingRepository.setState(state)
    }
}
