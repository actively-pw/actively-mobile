package com.actively.stubs

import com.actively.activity.Activity
import com.actively.activity.Location
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

fun stubActivity(
    id: String = "1",
    title: String? = "Morning activity",
    sport: String = "Cycling",
    start: Instant = Instant.fromEpochMilliseconds(0),
    stats: Activity.Stats = stubActivityStats(),
    route: List<Location> = stubRoute()
) = Activity(
    id = Activity.Id(id),
    title = title,
    sport = sport,
    start = start,
    stats = stats,
    route = route,
)

fun stubActivityStats(
    totalTime: Duration = 1.hours,
    distance: Double = 20.0,
    averageSpeed: Double = 20.0,
) = Activity.Stats(
    totalTime = totalTime,
    distance = distance,
    averageSpeed = averageSpeed
)

fun stubRoute(locationsNumber: Int = 2) = List(locationsNumber) {
    stubLocation(
        latitude = it.toDouble(),
        longitude = it.toDouble(),
        timestamp = Instant.fromEpochMilliseconds(it.toLong())
    )

}

fun stubLocation(
    timestamp: Instant = Instant.fromEpochMilliseconds(0),
    latitude: Double = 0.0,
    longitude: Double = 0.0,
) = Location(
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp
)
