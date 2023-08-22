package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository
import kotlinx.coroutines.flow.Flow

interface GetRecorderStateUseCase {

    operator fun invoke(): Flow<RecorderState>
}

class GetRecorderStateUseCaseImpl(
    private val activityRecordingRepository: ActivityRecordingRepository
) : GetRecorderStateUseCase {

    override fun invoke() = activityRecordingRepository.getState()

}
