package com.actively.stubs

import com.actively.activity.Activity
import com.actively.activity.Discipline
import com.actively.activity.RecordedActivity
import com.actively.activity.asString
import com.actively.http.dtos.RecordedActivityDto
import com.actively.http.dtos.StatsDto
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

fun stubRecordedActivity(
    id: String = "1",
    title: String = "Morning activity",
    sport: Discipline = Discipline.Cycling,
    start: Instant = Instant.fromEpochMilliseconds(0),
    stats: Activity.Stats = stubActivityStats(),
    routeUrl: String = "route://activity.net/$id",
    lightMapUrl: String = "image://activity.net/light/$id",
    darkMapUrl: String = "image://activity.net/dark/$id",
) = RecordedActivity(
    id = RecordedActivity.Id(id),
    title = title,
    start = start,
    sport = sport,
    stats = stats,
    routeUrl = routeUrl,
    lightMapUrl = lightMapUrl,
    darkMapUrl = darkMapUrl
)

fun stubRecordedActivityDto(
    id: String = "1",
    title: String = "Morning activity",
    sport: Discipline = Discipline.Cycling,
    start: Instant = Instant.fromEpochMilliseconds(0),
    stats: StatsDto = stubStatsDto(),
    routeUrl: String = "route://activity.net/$id",
    lightMapUrl: String = "image://activity.net/light/$id",
    darkMapUrl: String = "image://activity.net/dark/$id",
) = RecordedActivityDto(
    id = id,
    title = title,
    sport = sport.asString(),
    start = start,
    stats = stats,
    routeUrl = routeUrl,
    lightStaticMapUrl = lightMapUrl,
    darkStaticMapUrl = darkMapUrl,
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
