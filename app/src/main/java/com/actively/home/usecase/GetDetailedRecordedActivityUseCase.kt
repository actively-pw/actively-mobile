package com.actively.home.usecase

import com.actively.activity.DetailedRecordedActivity
import com.actively.activity.RecordedActivity
import com.actively.datasource.RecordedActivitiesDataSource

interface GetDetailedRecordedActivityUseCase {

    suspend operator fun invoke(id: RecordedActivity.Id): Result<DetailedRecordedActivity>
}

class GetDetailedRecordedActivityUseCaseImpl(
    private val dataSource: RecordedActivitiesDataSource
) : GetDetailedRecordedActivityUseCase {

    override suspend fun invoke(id: RecordedActivity.Id) = try {
        Result.success(dataSource.get(id))
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
