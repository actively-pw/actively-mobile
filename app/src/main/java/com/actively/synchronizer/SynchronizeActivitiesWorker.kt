package com.actively.synchronizer

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.actively.synchronizer.usecases.SynchronizeActivitiesUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SynchronizeActivitiesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val synchronizeActivitiesUseCase by inject<SynchronizeActivitiesUseCase>()

    override suspend fun doWork(): Result = try {
        synchronizeActivitiesUseCase()
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }

    companion object {
        const val SYNC_WORK_NAME = "sync-activities-work"
    }
}
