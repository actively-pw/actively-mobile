package com.actively.recorder.usecase

import android.content.Context
import android.content.Intent
import com.actively.recorder.RecordActivityService
import com.actively.repository.ActivityRecordingRepository
import kotlinx.datetime.Instant

interface ResumeRecordingUseCase {

    suspend operator fun invoke(resumedAt: Instant)
}

class ResumeRecordingUseCaseImpl(
    private val activityRecordingRepository: ActivityRecordingRepository,
    private val context: Context
) : ResumeRecordingUseCase {

    override suspend fun invoke(resumedAt: Instant) {
        activityRecordingRepository.insertEmptyRouteSlice(resumedAt)
        val resumeRecordingIntent = Intent(context, RecordActivityService::class.java).apply {
            action = RecordActivityService.RESUME_ACTION
            putExtra(RecordActivityService.TIMESTAMP_KEY, resumedAt.toString())
        }
        context.startForegroundService(resumeRecordingIntent)
    }
}
