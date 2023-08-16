package com.actively.location.usecase

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.repository.ActivityRepository

interface GetLastRouteLocationUseCase {

    suspend operator fun invoke(id: Activity.Id): Location?
}

class GetLastRouteLocationUseCaseImpl(
    private val activityRepository: ActivityRepository
) : GetLastRouteLocationUseCase {

    override suspend fun invoke(id: Activity.Id) = activityRepository.getLatestRouteLocation(id)
}


