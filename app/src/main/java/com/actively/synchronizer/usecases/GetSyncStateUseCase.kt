package com.actively.synchronizer.usecases

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.actively.synchronizer.SynchronizeActivitiesWorker
import com.actively.synchronizer.WorkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetSyncStateUseCase {

    operator fun invoke(): Flow<WorkState?>
}

class GetSyncStateUseCaseImpl(private val workManager: WorkManager) : GetSyncStateUseCase {

    override fun invoke() = workManager
        .getWorkInfosForUniqueWorkFlow(SynchronizeActivitiesWorker.SYNC_WORK_NAME)
        .map {
            it.firstOrNull()?.let { workInfo ->
                when {
                    workInfo.stopReason == WorkInfo.STOP_REASON_CONSTRAINT_CONNECTIVITY -> WorkState.NoInternetConnection
                    workInfo.state == WorkInfo.State.ENQUEUED -> WorkState.Enqueued
                    workInfo.state == WorkInfo.State.RUNNING -> WorkState.Running
                    else -> null
                }
            }
        }
}

