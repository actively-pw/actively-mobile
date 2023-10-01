package com.actively.synchronizer.usecases

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.actively.synchronizer.SynchronizeActivitiesWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetSyncWorkInfoUseCase {

    operator fun invoke(): Flow<WorkInfo?>
}

class GetSyncWorkInfoUseCaseImpl(private val workManager: WorkManager) : GetSyncWorkInfoUseCase {

    override fun invoke() = workManager
        .getWorkInfosForUniqueWorkFlow(SynchronizeActivitiesWorker.SYNC_WORK_NAME)
        .map { it.firstOrNull() }
}
