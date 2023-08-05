package com.actively.datasource

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.Route
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class ActivityDataSourceTest : FunSpec({

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    val database = ActivityDatabase(driver).apply {
        ActivityDatabase.Schema.create(driver)
    }

    test("Should insert and retrieve activity properly") {
        val activityDataSource = ActivityDataSourceImpl(database, testCoroutineScheduler)
        val expectedActivity = Activity(
            id = Activity.Id(1L),
            sport = "Cycling",
            start = Instant.fromEpochMilliseconds(0),
            totalTime = 1.hours,
            totalDistance = 23.5,
            averageSpeed = 20.3
        )
        activityDataSource.insertActivity(expectedActivity)
        activityDataSource.getActivity(id = Activity.Id(1L)).shouldBe(expectedActivity)
    }

    test("Should return list of all activities") {
        val activityDataSource = ActivityDataSourceImpl(database, testCoroutineScheduler)
        val expectedActivities = listOf(
            Activity(
                id = Activity.Id(1L),
                sport = "Cycling",
                start = Instant.fromEpochMilliseconds(0),
                totalTime = 1.hours,
                totalDistance = 23.5,
                averageSpeed = 20.3
            ),
            Activity(
                id = Activity.Id(2L),
                sport = "Running",
                start = Instant.fromEpochMilliseconds(0),
                totalTime = 2.hours,
                totalDistance = 20.0,
                averageSpeed = 10.0
            )
        )
        expectedActivities.forEach {
            activityDataSource.insertActivity(it)
        }
        activityDataSource.getActivities().first().shouldBe(expectedActivities)
        testCoroutineScheduler.advanceUntilIdle()
    }

    test("Should insert and retrieve Route properly") {
        val activityDataSource = ActivityDataSourceImpl(database, testCoroutineScheduler)
        val expectedRoute = Route(
            id = Route.Id(1L),
            activityId = Activity.Id(1L),
            locations = listOf(
                Location(1.0, 1.0, Instant.fromEpochMilliseconds(0)),
                Location(2.0, 2.0, Instant.fromEpochMilliseconds(0)),
                Location(3.0, 3.0, Instant.fromEpochMilliseconds(0)),
                Location(4.0, 4.0, Instant.fromEpochMilliseconds(0)),
            )
        )
        activityDataSource.insertRoute(expectedRoute)
        activityDataSource.getRoute(id = Activity.Id(1L)).shouldBe(expectedRoute)
    }

    test("Should insert and retrieve Location points properly") {
        val activityDataSource = ActivityDataSourceImpl(database, testCoroutineScheduler)
        val expectedLocations = listOf(
            Location(1.0, 1.0, Instant.fromEpochMilliseconds(0)),
            Location(2.0, 2.0, Instant.fromEpochMilliseconds(0)),
            Location(3.0, 3.0, Instant.fromEpochMilliseconds(0)),
            Location(4.0, 4.0, Instant.fromEpochMilliseconds(0)),
        )
        expectedLocations.forEach {
            activityDataSource.insertLocation(location = it, routeId = Route.Id(2L))
        }
        activityDataSource.getRouteLocations(id = Route.Id(2L)).shouldBe(expectedLocations)
    }

}) {
    init {
        coroutineTestScope = true
    }
}
