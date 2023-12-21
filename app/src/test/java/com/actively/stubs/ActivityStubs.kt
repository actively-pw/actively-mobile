package com.actively.stubs

import com.actively.activity.Activity
import com.actively.activity.Discipline
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.distance.Distance
import com.actively.distance.Distance.Companion.kilometers
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

fun stubActivity(
    id: String = "1",
    title: String? = "Morning activity",
    sport: Discipline = Discipline.Cycling,
    stats: Activity.Stats = stubActivityStats(),
    route: List<RouteSlice> = stubRoute()
) = Activity(
    id = Activity.Id(id),
    title = title,
    sport = sport,
    stats = stats,
    route = route,
)

fun stubActivityStats(
    totalTime: Duration = 1.hours,
    distance: Distance = 20.0.kilometers,
    averageSpeed: Double = 20.0,
) = Activity.Stats(
    totalTime = totalTime,
    distance = distance,
    averageSpeed = averageSpeed
)

fun stubRoute(numberOfSlices: Int = 4) = List(numberOfSlices) {
    stubRouteSlice(start = Instant.fromEpochMilliseconds(0) + (1.hours * it))
}

fun stubRouteSlice(
    start: Instant = Instant.fromEpochMilliseconds(0)
) = RouteSlice(
    start = start,
    locations = List(5) { stubLocation() }
)


fun stubLocation(
    timestamp: Instant = Instant.fromEpochMilliseconds(0),
    latitude: Double = 0.0,
    longitude: Double = 0.0,
) = Location(
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp
)
