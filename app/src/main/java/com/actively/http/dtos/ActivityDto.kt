package com.actively.http.dtos

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.distance.Distance.Companion.inKilometers
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ActivityDto(
    val id: String,
    val title: String,
    val sport: String,
    val stats: StatsDto,
    val route: List<RouteSliceDto>,
)

@Serializable
data class StatsDto(
    val duration: Long,
    val distance: Double,
    val averageSpeed: Double,
)

@Serializable
data class RouteSliceDto(
    val start: Instant,
    val locations: List<LocationDto>
)

@Serializable
data class LocationDto(
    val timestamp: Instant,
    val latitude: Double,
    val longitude: Double,
)

fun Activity.toDto() = ActivityDto(
    id = id.value,
    title = title ?: "",
    sport = sport,
    stats = stats.toDto(),
    route = route.map(RouteSlice::toDto)
)

fun Activity.Stats.toDto() = StatsDto(
    duration = totalTime.inWholeMilliseconds,
    distance = distance.inKilometers,
    averageSpeed = averageSpeed
)

fun RouteSlice.toDto() = RouteSliceDto(
    start = start,
    locations = locations.map(Location::toDto)
)

fun Location.toDto() = LocationDto(timestamp, latitude, longitude)




