package com.actively.recorder.usecase

import com.actively.repository.ActivityRecordingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

class GetStatsUseCase(
    private val activityRecordingRepository: ActivityRecordingRepository
) {

    operator fun invoke(interval: Duration) = flow {
        var totalTime = activityRecordingRepository.getStats().first().totalTime
        while (true) {
            totalTime += interval
            delay(interval)
            emit(totalTime)
        }
    }.combine(activityRecordingRepository.getStats()) { totalTime, stats ->
        stats.copy(totalTime = totalTime)
    }
}

