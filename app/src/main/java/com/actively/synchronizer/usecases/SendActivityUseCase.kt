package com.actively.synchronizer.usecases

import com.actively.activity.Activity
import com.actively.repository.ActivityRecordingRepository

interface SendActivityUseCase {

    suspend operator fun invoke(activity: Activity): Result<Unit>
}

class SendActivityUseCaseImpl(
    private val recordingRepository: ActivityRecordingRepository
) : SendActivityUseCase {

    override suspend fun invoke(activity: Activity) = try {
        recordingRepository.syncActivity(activity)
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
