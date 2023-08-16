package com.actively.location.usecase

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.repository.ActivityRepository

interface InsertLocationUseCase {

    suspend operator fun invoke(location: Location, id: Activity.Id)
}

class InsertLocationUseCaseImpl(
    private val activityRepository: ActivityRepository
) : InsertLocationUseCase {

    override suspend fun invoke(location: Location, id: Activity.Id) = activityRepository
        .insertLocation(location, id)
}
