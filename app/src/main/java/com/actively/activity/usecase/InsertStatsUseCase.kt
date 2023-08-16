package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.repository.ActivityRepository

interface InsertStatsUseCase {

    suspend operator fun invoke(stats: Activity.Stats, id: Activity.Id)
}

class InsertStatsUseCaseImpl(
    private val activityRepository: ActivityRepository
) : InsertStatsUseCase {

    override suspend fun invoke(stats: Activity.Stats, id: Activity.Id) = activityRepository
        .insertStats(stats, id)
}
