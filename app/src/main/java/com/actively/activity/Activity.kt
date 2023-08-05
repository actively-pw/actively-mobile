package com.actively.activity

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class Activity(
    val id: Id,
    val title: String?,
    val sport: String,
    val start: Instant,
    val stats: Stats,
    val route: List<Location>
) {

    data class Id(val value: String)

    data class Stats(
        val totalTime: Duration,
        val distance: Double,
        val averageSpeed: Double
    )
}

data class Location(
    val timestamp: Instant,
    val latitude: Double,
    val longitude: Double
)
