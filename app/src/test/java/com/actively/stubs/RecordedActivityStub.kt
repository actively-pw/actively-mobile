package com.actively.stubs

import com.actively.http.dtos.RecordedActivityDto
import com.actively.http.dtos.StatsDto
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

fun stubRecordedActivityDto(
    id: String = "1",
    title: String = "Morning activity",
    sport: Int = 0,
    start: Instant = Instant.fromEpochMilliseconds(0),
    stats: StatsDto = stubStatsDto(),
    routeUrl: String = "route://activity.net/$id"
) = RecordedActivityDto(
    id = id,
    title = title,
    sport = sport,
    start = start,
    stats = stats,
    routeUrl = routeUrl
)

fun stubStatsDto(
    distanceKilometers: Double = 100.0,
    averageSpeed: Double = 20.0,
    totalTime: Duration = 5.hours
) = StatsDto(
    duration = totalTime.inWholeMilliseconds,
    averageSpeed = averageSpeed,
    distance = distanceKilometers
)