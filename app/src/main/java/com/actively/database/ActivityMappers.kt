package com.actively.database

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.Route
import database.ActivityEntity
import database.LocationEntity
import database.RouteEntity
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds

fun ActivityEntity.toActivity() = Activity(
    id = Activity.Id(id),
    sport = sport,
    start = Instant.fromEpochMilliseconds(timestamp),
    totalTime = totalTime.milliseconds,
    totalDistance = totalDistance,
    averageSpeed = averageSpeed,
)

fun List<ActivityEntity>.toActivityList() = map(ActivityEntity::toActivity)

fun RouteEntity.toRoute(locations: List<Location>) = Route(
    id = Route.Id(id),
    activityId = Activity.Id(activityId),
    locations = locations
)

fun LocationEntity.toLocation() = Location(
    longitude = longitute,
    latitude = latitude,
    timestamp = Instant.fromEpochMilliseconds(timestamp)
)
