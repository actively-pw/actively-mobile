package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.distance.Distance.Companion.plus
import com.actively.location.LocationProvider
import com.actively.repository.ActivityRecordingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

interface RecordActivityUseCase {

    operator fun invoke(start: Instant): Flow<Activity.Stats>
}

class RecordActivityUseCaseImpl(
    private val locationProvider: LocationProvider,
    private val activityRecordingRepository: ActivityRecordingRepository,
) : RecordActivityUseCase {

    override operator fun invoke(start: Instant) = locationProvider
        .userLocation(
            updateInterval = 3.seconds,
            fastestUpdateInterval = 1.seconds,
            locationUpdatesDistance = 2.meters
        )
        .map { currentLocation ->
            activityRecordingRepository.getLatestRouteLocation() to currentLocation
        }
        .mapNotNull { (lastLocation, currentLocation) ->
            val prevStats = activityRecordingRepository
                .getStats()
                .firstOrNull() ?: return@mapNotNull null
            val currentStats = prevStats.update(lastLocation, currentLocation, start)
            activityRecordingRepository.insertStats(stats = currentStats)
            activityRecordingRepository.insertLocation(currentLocation)
            currentStats
        }

    private fun Activity.Stats.update(
        lastLocation: Location?,
        currentLocation: Location,
        start: Instant
    ): Activity.Stats {
        val elapsedTime = when (lastLocation) {
            null -> currentLocation.timestamp - start
            else -> currentLocation.timestamp - lastLocation.timestamp
        }.coerceAtLeast(0.seconds)
        val totalTime = totalTime + elapsedTime
        val traveledDistance = lastLocation?.distanceTo(currentLocation) ?: 0.meters
        val totalDistance = distance + traveledDistance
        val averageSpeed = when (elapsedTime) {
            0.seconds -> 0.0
            else -> totalDistance.inKilometers / totalTime.toDouble(DurationUnit.HOURS)
        }
        return copy(totalTime = totalTime, distance = totalDistance, averageSpeed = averageSpeed)
    }
}
