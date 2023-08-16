package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

interface GetStatsUseCase {

    operator fun invoke(id: Activity.Id): Flow<Activity.Stats>
}

class GetStatsUseCaseImpl(private val activityRepository: ActivityRepository) : GetStatsUseCase {

    override fun invoke(id: Activity.Id) = activityRepository.getStats(id)
}
