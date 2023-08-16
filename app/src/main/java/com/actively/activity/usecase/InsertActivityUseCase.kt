package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.repository.ActivityRepository

interface InsertActivityUseCase {

    suspend operator fun invoke(activity: Activity)
}

class InsertActivityUseCaseImpl(
    private val activityRepository: ActivityRepository
) : InsertActivityUseCase {

    override suspend fun invoke(activity: Activity) = activityRepository.insertActivity(activity)
}
