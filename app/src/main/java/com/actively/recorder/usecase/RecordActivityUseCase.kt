package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.distance.Distance.Companion.plus
import com.actively.location.LocationProvider
import com.actively.repository.ActivityRecordingRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

interface RecordActivityUseCase {

    operator fun invoke(start: Instant): Flow<Activity.Stats>
}

class RecordActivityUseCaseImpl(
    private val locationProvider: LocationProvider,
    private val activityRecordingRepository: ActivityRecordingRepository,
) : RecordActivityUseCase {

    override operator fun invoke(start: Instant) = totalActivityTimeFlow(interval = 1.seconds)
        .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
        .combine(distanceGainFlow()) { totalTime, distanceGain ->
            val prevStats = activityRecordingRepository
                .getStats()
                .first()
            val totalDistance = prevStats.distance + distanceGain
            val updatedStats = prevStats.copy(
                totalTime = totalTime,
                distance = totalDistance,
                averageSpeed = totalDistance.inKilometers / totalTime.toDouble(DurationUnit.HOURS)
            )
            activityRecordingRepository.insertStats(updatedStats)
            updatedStats
        }


    private fun totalActivityTimeFlow(interval: Duration) = flow {
        var totalTime = activityRecordingRepository.getStats().first().totalTime
        while (true) {
            totalTime += interval
            delay(interval)
            // todo: this for some reason stops emmiting when app is in background for some time
            // todo: emit current time every 1.seconds. Then calculate time that passed to this moment and return from flow
            emit(totalTime)
        }
    }

    private fun distanceGainFlow() = locationProvider
        .userLocation(
            updateInterval = 4.seconds,
            fastestUpdateInterval = 2.seconds,
            locationUpdatesDistance = 1.meters
        )
        .map { currentLocation ->
            val lastLocation = activityRecordingRepository.getLatestRouteLocation()
            activityRecordingRepository.insertLocation(currentLocation)
            lastLocation?.distanceTo(currentLocation) ?: 0.meters
        }
}
