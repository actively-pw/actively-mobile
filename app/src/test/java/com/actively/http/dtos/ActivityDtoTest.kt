package com.actively.http.dtos

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.distance.Distance.Companion.meters
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class ActivityDtoTest : FunSpec({

    test("Should properly map Location to LocationDto") {
        val expectedDto = LocationDto(
            longitude = 1.234,
            latitude = 4.512,
            timestamp = Instant.fromEpochMilliseconds(1234)
        )
        val location = Location(
            longitude = 1.234,
            latitude = 4.512,
            timestamp = Instant.fromEpochMilliseconds(1234)
        )
        location.toDto() shouldBe expectedDto
    }

    test("Should properly map RouteSlice to RouteSliceDto") {
        val expectedRouteSliceDto = RouteSliceDto(
            start = Instant.fromEpochMilliseconds(1500),
            locations = listOf(
                LocationDto(Instant.fromEpochMilliseconds(100), 1.0, 2.0),
                LocationDto(Instant.fromEpochMilliseconds(200), 3.0, 5.4),
                LocationDto(Instant.fromEpochMilliseconds(400), 1.5, 6.7),
            )
        )
        val routeSlice = RouteSlice(
            start = Instant.fromEpochMilliseconds(1500),
            locations = listOf(
                Location(Instant.fromEpochMilliseconds(100), 1.0, 2.0),
                Location(Instant.fromEpochMilliseconds(200), 3.0, 5.4),
                Location(Instant.fromEpochMilliseconds(400), 1.5, 6.7),
            )
        )
        routeSlice.toDto() shouldBe expectedRouteSliceDto
    }

    test("Should properly map Activity.Stats to StatsDto") {
        val expectedStatsDto = StatsDto(
            duration = 51000,
            distance = 0.54,
            averageSpeed = 4.1
        )
        val stats = Activity.Stats(
            totalTime = 51.seconds,
            distance = 540.meters,
            averageSpeed = 4.1
        )
        stats.toDto() shouldBe expectedStatsDto
    }

    test("Should properly map Activity to ActivityDto") {
        val expectedActivityDto = ActivityDto(
            id = "1",
            title = "Morning activity",
            sport = 0,
            stats = StatsDto(
                duration = 51000,
                distance = 0.54,
                averageSpeed = 4.1
            ),
            route = listOf(
                RouteSliceDto(
                    start = Instant.fromEpochMilliseconds(500),
                    locations = listOf(
                        LocationDto(Instant.fromEpochMilliseconds(100), 1.0, 2.0),
                        LocationDto(Instant.fromEpochMilliseconds(200), 3.0, 5.4),
                        LocationDto(Instant.fromEpochMilliseconds(400), 1.5, 6.7),
                    )
                ),
                RouteSliceDto(
                    start = Instant.fromEpochMilliseconds(1500),
                    locations = listOf(
                        LocationDto(Instant.fromEpochMilliseconds(600), 1.1, 2.1),
                        LocationDto(Instant.fromEpochMilliseconds(700), 3.5, 5.3),
                        LocationDto(Instant.fromEpochMilliseconds(800), 1.2, 6.8),
                    )
                )
            )
        )
        val activity = Activity(
            id = Activity.Id("1"),
            title = "Morning activity",
            sport = "Cycling",
            stats = Activity.Stats(51.seconds, 540.meters, 4.1),
            route = listOf(
                RouteSlice(
                    start = Instant.fromEpochMilliseconds(500),
                    locations = listOf(
                        Location(Instant.fromEpochMilliseconds(100), 1.0, 2.0),
                        Location(Instant.fromEpochMilliseconds(200), 3.0, 5.4),
                        Location(Instant.fromEpochMilliseconds(400), 1.5, 6.7),
                    )
                ),
                RouteSlice(
                    start = Instant.fromEpochMilliseconds(1500),
                    locations = listOf(
                        Location(Instant.fromEpochMilliseconds(600), 1.1, 2.1),
                        Location(Instant.fromEpochMilliseconds(700), 3.5, 5.3),
                        Location(Instant.fromEpochMilliseconds(800), 1.2, 6.8),
                    )
                )
            )
        )
        activity.toDto() shouldBe expectedActivityDto
    }
})
