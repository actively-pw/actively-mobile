package com.actively.activity

import com.actively.distance.Distance
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
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
) {

    fun toPoint(): Point = Point.fromLngLat(longitude, latitude)

    fun distanceTo(other: Location) = TurfMeasurement.distance(
        this.toPoint(),
        other.toPoint(),
        TurfConstants.UNIT_METERS
    ).toLong().meters
}
