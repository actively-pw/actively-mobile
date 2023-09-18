package com.actively.synchronizer.usecases

import com.actively.activity.Activity
import com.actively.repository.ActivityRecordingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

interface SynchronizeActivitiesUseCase {

    suspend operator fun invoke()
}

@OptIn(ExperimentalCoroutinesApi::class)
class SynchronizeActivitiesUseCaseImpl(
    private val sendActivityUseCase: SendActivityUseCase,
    private val recordingRepository: ActivityRecordingRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SynchronizeActivitiesUseCase {

    override suspend operator fun invoke() = withContext(dispatcher) {
        recordingRepository.getRecordedActivitiesId()
            .asFlow()
            .flatMapMerge(concurrency = SYNC_CONCURRENCY) { id -> synchronizeActivity(id) }
            .onEach { recordingRepository.removeActivities(listOf(it)) }
            .collect()
    }

    private fun synchronizeActivity(id: Activity.Id) = flow {
        val activity = recordingRepository.getActivity(id) ?: return@flow
        val result = sendActivityUseCase(activity)
        result.getOrThrow()
        emit(id)
    }

    private companion object {

        const val SYNC_CONCURRENCY = 4
    }
}
