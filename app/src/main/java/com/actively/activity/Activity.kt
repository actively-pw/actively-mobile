package com.actively.activity

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class Activity(
    val id: Id,
    val sport: String,
    val start: Instant,
    val totalTime: Duration,
    val totalDistance: Double,
    val averageSpeed: Double,
) {
    data class Id(val value: Long)
}

data class Route(
    val id: Id,
    val activityId: Activity.Id,
    val locations: List<Location>
) {

    data class Id(val value: Long)
}

data class Location(
    val longitude: Double,
    val latitude: Double,
    val timestamp: Instant
)
