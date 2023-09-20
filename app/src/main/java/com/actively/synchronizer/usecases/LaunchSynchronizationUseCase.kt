package com.actively.synchronizer.usecases


import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.actively.synchronizer.SynchronizeActivitiesWorker
import java.time.Duration

interface LaunchSynchronizationUseCase {

    operator fun invoke()
}

class LaunchSynchronizationUseCaseImpl(
    private val workManager: WorkManager
) : LaunchSynchronizationUseCase {

    override fun invoke() {
        workManager.enqueueUniqueWork(
            SynchronizeActivitiesWorker.SYNC_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            synchronizationRequest()
        )
    }

    private fun synchronizationRequest() = OneTimeWorkRequestBuilder<SynchronizeActivitiesWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofMinutes(10))
        .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
        .build()
}
