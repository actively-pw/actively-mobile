package com.actively.details.usecase

import com.actively.activity.DynamicMapData
import com.actively.activity.RecordedActivity
import com.actively.repository.RecordedActivitiesRepository

interface GetDynamicMapDataUseCase {

    suspend operator fun invoke(id: RecordedActivity.Id): Result<DynamicMapData>
}

class GetDynamicMapDataUseCaseImpl(
    private val repository: RecordedActivitiesRepository
) : GetDynamicMapDataUseCase {

    override suspend fun invoke(id: RecordedActivity.Id) = try {
        Result.success(repository.getDynamicMapData(id))
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
