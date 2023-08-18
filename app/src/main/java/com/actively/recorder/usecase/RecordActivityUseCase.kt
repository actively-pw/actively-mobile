package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.kilometers
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

    operator fun invoke(id: Activity.Id, start: Instant): Flow<Activity.Stats>
}

class RecordActivityUseCaseImpl(
    private val locationProvider: LocationProvider,
    private val activityRecordingRepository: ActivityRecordingRepository,
) : RecordActivityUseCase {

    override operator fun invoke(id: Activity.Id, start: Instant) = locationProvider
        .userLocation(
            updateInterval = 3.seconds,
            fastestUpdateInterval = 1.seconds,
            locationUpdatesDistance = 2.meters
        )
        .map { currentLocation ->
            activityRecordingRepository.getLatestRouteLocation(id) to currentLocation
        }
        .mapNotNull { (lastLocation, currentLocation) ->
            val prevStats = activityRecordingRepository
                .getStats(id)
                .firstOrNull() ?: return@mapNotNull null
            val currentStats = prevStats.update(lastLocation, currentLocation, start)
            activityRecordingRepository.insertStats(stats = currentStats, id = id)
            activityRecordingRepository.insertLocation(currentLocation, id)
            currentStats
        }

    private fun Activity.Stats.update(
        lastLocation: Location?,
        currentLocation: Location,
        start: Instant
    ): Activity.Stats {
        val elapsedTime = (currentLocation.timestamp - start).coerceAtLeast(0.seconds)
        val traveledDistance = lastLocation?.distanceTo(currentLocation) ?: 0.kilometers
        val totalDistance = distance + traveledDistance
        val averageSpeed = when (elapsedTime) {
            0.seconds -> 0.0
            else -> totalDistance.inKilometers / elapsedTime.toDouble(DurationUnit.HOURS)
        }
        return copy(totalTime = elapsedTime, distance = totalDistance, averageSpeed = averageSpeed)
    }
}
