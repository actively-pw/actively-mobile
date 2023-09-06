package com.actively.datasource

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.RouteSlice
import com.actively.distance.Distance.Companion.kilometers
import com.actively.recorder.RecorderState
import com.actively.stubs.stubActivity
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class ActivityRecordingDataSourceTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerTest

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    val database = ActivityDatabase(driver).apply {
        ActivityDatabase.Schema.create(driver)
    }

    context("ActivityDataSourceTest") {
        val activityDataSource = ActivityRecordingDataSourceImpl(database, testCoroutineScheduler)

        test("Should insert and retrieve activity") {
            val activity = stubActivity(id = "1", route = emptyList())
            activityDataSource.insertActivity(
                activity.id,
                activity.title,
                activity.sport,
                activity.stats
            )
            activityDataSource.getActivity() shouldBe activity
        }

        test("Should return 0 if no activities found in database") {
            activityDataSource.getActivityCount() shouldBe 0
        }

        test("Maximum of 1 activity can be present in database") {
            activityDataSource.getActivityCount() shouldBe 0
            val activity = stubActivity(id = "1", route = emptyList())
            activityDataSource.insertActivity(
                activity.id,
                activity.title,
                activity.sport,
                activity.stats
            )
            activityDataSource.getActivityCount() shouldBe 1
            val otherActivity = stubActivity(id = "2", route = emptyList())
            activityDataSource.insertActivity(
                otherActivity.id,
                otherActivity.title,
                otherActivity.sport,
                otherActivity.stats
            )
            activityDataSource.getActivityCount() shouldBe 1
        }

        test("Should always replace activity present in database") {
            val activity = stubActivity(id = "1", route = emptyList())
            activityDataSource.insertActivity(
                activity.id,
                activity.title,
                activity.sport,
                activity.stats
            )
            val expected = stubActivity(id = "2", route = emptyList())
            activityDataSource.insertActivity(
                expected.id,
                expected.title,
                expected.sport,
                expected.stats
            )
            activityDataSource.getActivity() shouldBe expected
        }

        test("Should return null Activity if none were found") {
            activityDataSource.getActivity().shouldBeNull()
        }

        test("Should insert and retrieve activity stats") {
            val stats = stubActivityStats()
            activityDataSource.insertStats(stats = stats)
            activityDataSource.getStats().first() shouldBe stats
        }

        test("Should replace already saved stats") {
            val stats = stubActivityStats()
            activityDataSource.insertStats(stats)
            val newStats = stubActivityStats(
                totalTime = 2.hours,
                distance = 25.kilometers,
                averageSpeed = 15.0
            )
            activityDataSource.insertStats(newStats)
            activityDataSource.getStats().first() shouldBe newStats
        }

        test("Should return empty Activity.Stats if none were found") {
            activityDataSource.getStats().first() shouldBe Activity.Stats.empty()
        }

        test("Should insert empty RouteSlice") {
            activityDataSource.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(0))
            activityDataSource.getRoute().first() shouldBe listOf(
                RouteSlice(start = Instant.fromEpochMilliseconds(0), locations = emptyList())
            )
        }

        test("Should insert and retrieve route properly with slices and locations ordered from the oldest") {
            val firstSliceLocations = listOf(
                stubLocation(Instant.fromEpochMilliseconds(0)),
                stubLocation(Instant.fromEpochMilliseconds(10)),
                stubLocation(Instant.fromEpochMilliseconds(20)),
            )
            val secondsSliceLocations = listOf(
                stubLocation(Instant.fromEpochMilliseconds(100)),
                stubLocation(Instant.fromEpochMilliseconds(120)),
                stubLocation(Instant.fromEpochMilliseconds(240)),
            )
            activityDataSource.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(0))
            firstSliceLocations.reversed().forEach {
                activityDataSource.insertLocationToLatestRouteSlice(it)
            }
            activityDataSource.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(100))
            secondsSliceLocations.reversed().forEach {
                activityDataSource.insertLocationToLatestRouteSlice(it)
            }
            val expectedRoute = listOf(
                RouteSlice(
                    start = Instant.fromEpochMilliseconds(0),
                    locations = firstSliceLocations
                ),
                RouteSlice(
                    start = Instant.fromEpochMilliseconds(100),
                    locations = secondsSliceLocations
                )
            )
            activityDataSource.getRoute().first() shouldBe expectedRoute
        }

        test("getLatestLocationFromLastRouteSlice should get latest location from latest route slice") {
            activityDataSource.insertEmptyRouteSlice(start = Instant.fromEpochMilliseconds(0))
            val expectedLocation = stubLocation(Instant.fromEpochMilliseconds(240))
            val firstSliceLocations = listOf(
                stubLocation(Instant.fromEpochMilliseconds(0)),
                stubLocation(Instant.fromEpochMilliseconds(10)),
                stubLocation(Instant.fromEpochMilliseconds(2000)),
            )
            val secondsSliceLocations = listOf(
                stubLocation(Instant.fromEpochMilliseconds(100)),
                stubLocation(Instant.fromEpochMilliseconds(120)),
                expectedLocation,
            )
            activityDataSource.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(0))
            firstSliceLocations.forEach {
                activityDataSource.insertLocationToLatestRouteSlice(it)
            }
            activityDataSource.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(100))
            secondsSliceLocations.forEach {
                activityDataSource.insertLocationToLatestRouteSlice(it)
            }
            activityDataSource.getLatestLocationFromLastRouteSlice() shouldBe expectedLocation
        }

        test("getLatestLocationFromLastRouteSlice should return null if no location was found in latest route slice") {
            activityDataSource.insertEmptyRouteSlice(start = Instant.fromEpochMilliseconds(0))
            val firstSliceLocations = listOf(
                stubLocation(Instant.fromEpochMilliseconds(0)),
                stubLocation(Instant.fromEpochMilliseconds(10)),
                stubLocation(Instant.fromEpochMilliseconds(2000)),
            )
            activityDataSource.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(0))
            firstSliceLocations.forEach {
                activityDataSource.insertLocationToLatestRouteSlice(it)
            }
            activityDataSource.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(100))
            activityDataSource.getLatestLocationFromLastRouteSlice().shouldBeNull()
        }

        test("getLatestLocationFromLastRouteSlice should return null if no RouteSlices were found") {
            activityDataSource.getLatestLocationFromLastRouteSlice().shouldBeNull()
        }

        test("setState sets state of recorder correctly") {
            val states = listOf(
                RecorderState.Idle,
                RecorderState.Started,
                RecorderState.Paused,
                RecorderState.Stopped
            )
            states.forEach { state ->
                activityDataSource.setState(state)
                activityDataSource.getState().first() shouldBe state
            }
        }

        test("getState returns RecorderState.Idle if no state was found in database") {
            activityDataSource.getState().first() shouldBe RecorderState.Idle
        }

        test("updateStats should pass currently saved stats to transform function") {
            val stats = stubActivityStats()
            activityDataSource.insertStats(stats)
            activityDataSource.updateStats { actual ->
                actual shouldBe stats
            }
        }

        test("updateStats should pass empty stats to transform function if no stats were found in db") {
            activityDataSource.updateStats { actual ->
                actual shouldBe Activity.Stats.empty()
            }
        }

        test("updateStats should save updated stats to database") {
            activityDataSource.insertStats(stubActivityStats())
            val expected = stubActivityStats(
                totalTime = 10.hours,
                distance = 100.kilometers,
                averageSpeed = 1.0
            )
            activityDataSource.updateStats { expected }
            activityDataSource.getStats().first() shouldBe expected
        }
    }
})
