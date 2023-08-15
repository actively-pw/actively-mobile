package com.actively.activity

import com.actively.distance.Distance
import com.actively.distance.Distance.Companion.kilometers
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

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
        val distance: Distance,
        val averageSpeed: Double
    ) {

        companion object {

            fun empty() = Stats(totalTime = 0.hours, distance = 0.kilometers, averageSpeed = 0.0)
        }
    }
}

data class Location(
    val timestamp: Instant,
    val latitude: Double,
    val longitude: Double
)
