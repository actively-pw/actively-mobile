package com.actively.recorder.usecase

import android.content.Context
import android.content.Intent
import com.actively.activity.Discipline
import com.actively.activity.usecase.CreateActivityUseCase
import com.actively.recorder.RecordActivityService
import com.actively.repository.ActivityRecordingRepository
import kotlinx.datetime.Instant

/**
 * Usecase that starts recording of activity. It sends Intent to [RecordActivityService].
 */
interface StartRecordingUseCase {

    /**
     * Launches activity recording.
     *
     * @param sport - discipline to record
     * @param startedAt - timestamp of recording start
     */
    suspend operator fun invoke(sport: Discipline, startedAt: Instant)
}

class StartRecordingUseCaseImpl(
    private val createActivityUseCase: CreateActivityUseCase,
    private val activityRecordingRepository: ActivityRecordingRepository,
    private val context: Context,
) : StartRecordingUseCase {

    override suspend fun invoke(sport: Discipline, startedAt: Instant) {
        val activity = createActivityUseCase(sport = sport)
        activityRecordingRepository.insertRoutelessActivity(activity)
        activityRecordingRepository.insertEmptyRouteSlice(startedAt)
        val startRecordingIntent = Intent(context, RecordActivityService::class.java).apply {
            action = RecordActivityService.START_ACTON
            putExtra(RecordActivityService.TIMESTAMP_KEY, startedAt.toString())
        }
        context.startForegroundService(startRecordingIntent)
    }
}
