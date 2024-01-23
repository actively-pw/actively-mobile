package com.actively.home.usecase

import com.actively.activity.DetailedRecordedActivity
import com.actively.activity.RecordedActivity
import com.actively.repository.RecordedActivitiesRepository

/**
 * Gets recorded activity details
 */
interface GetDetailedRecordedActivityUseCase {

    suspend operator fun invoke(id: RecordedActivity.Id): Result<DetailedRecordedActivity>
}

class GetDetailedRecordedActivityUseCaseImpl(
    private val dataSource: RecordedActivitiesRepository
) : GetDetailedRecordedActivityUseCase {

    override suspend fun invoke(id: RecordedActivity.Id) = try {
        Result.success(dataSource.getDetailedActivity(id))
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
