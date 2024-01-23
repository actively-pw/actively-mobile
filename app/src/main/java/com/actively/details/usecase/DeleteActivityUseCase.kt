package com.actively.details.usecase

import com.actively.activity.RecordedActivity
import com.actively.repository.RecordedActivitiesRepository

/**
 * Permanently deletes recorded activity.
 */
interface DeleteActivityUseCase {

    suspend operator fun invoke(id: RecordedActivity.Id): Result<Unit>
}

class DeleteActivityUseCaseImpl(
    private val repository: RecordedActivitiesRepository
) : DeleteActivityUseCase {

    override suspend fun invoke(id: RecordedActivity.Id) = try {
        repository.deleteActivity(id)
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
