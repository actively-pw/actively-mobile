package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.usecase.GetStatsUseCase
import com.actively.activity.usecase.InsertStatsUseCase
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.plus
import com.actively.location.usecase.GetLastRouteLocationUseCase
import com.actively.location.usecase.GetUserLocationUpdatesUseCase
import com.actively.location.usecase.InsertLocationUseCase
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
    private val getUserLocationUpdates: GetUserLocationUpdatesUseCase,
    private val getLatestRouteLocation: GetLastRouteLocationUseCase,
    private val getStats: GetStatsUseCase,
    private val insertStats: InsertStatsUseCase,
    private val insertLocation: InsertLocationUseCase
) : RecordActivityUseCase {

    override operator fun invoke(id: Activity.Id, start: Instant) = getUserLocationUpdates()
        .map { currentLocation ->
            getLatestRouteLocation(id) to currentLocation
        }
        .mapNotNull { (lastLocation, currentLocation) ->
            val prevStats = getStats(id).firstOrNull() ?: return@mapNotNull null
            val currentStats = prevStats.update(lastLocation, currentLocation, start)
            insertStats(stats = currentStats, id = id)
            insertLocation(currentLocation, id)
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
