package com.actively.datasource

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.RouteSlice
import com.actively.distance.Distance.Companion.kilometers
import com.actively.stubs.stubActivity
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import io.kotest.assertions.throwables.shouldThrow
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
            activityDataSource.getActivity(id = activity.id) shouldBe activity
        }

        test("Should return null Activity if none were found") {
            activityDataSource.getActivity(id = Activity.Id("1")).shouldBeNull()
        }

        test("Should insert and retrieve activity stats") {
            val stats = stubActivityStats()
            activityDataSource.insertStats(stats = stats, id = Activity.Id("1"))
            activityDataSource.getStats(id = Activity.Id("1")).first() shouldBe stats
        }

        test("Should replace already saved stats") {
            val stats = stubActivityStats()
            val activityId = Activity.Id("1")
            activityDataSource.insertStats(stats, activityId)
            val newStats = stubActivityStats(
                totalTime = 2.hours,
                distance = 25.kilometers,
                averageSpeed = 15.0
            )
            activityDataSource.insertStats(newStats, activityId)
            activityDataSource.getStats(activityId).first() shouldBe newStats
        }

        test("Should throw exception if none Activity.Stats were found") {
            shouldThrow<Exception> {
                activityDataSource.getStats(id = Activity.Id("1")).first()
            }
        }

        test("Should insert empty RouteSlice") {
            val id = Activity.Id("1")
            activityDataSource.insertEmptyRouteSlice(id, Instant.fromEpochMilliseconds(0))
            activityDataSource.getRoute(id).first() shouldBe listOf(
                RouteSlice(start = Instant.fromEpochMilliseconds(0), locations = emptyList())
            )
        }

        test("Should insert and retrieve route properly with slices and locations ordered from the oldest") {
            val id = Activity.Id("1")
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
            activityDataSource.insertEmptyRouteSlice(id, Instant.fromEpochMilliseconds(0))
            firstSliceLocations.reversed().forEach {
                activityDataSource.insertLocationToLatestRouteSlice(id, it)
            }
            activityDataSource.insertEmptyRouteSlice(id, Instant.fromEpochMilliseconds(100))
            secondsSliceLocations.reversed().forEach {
                activityDataSource.insertLocationToLatestRouteSlice(id, it)
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
            activityDataSource.getRoute(id).first() shouldBe expectedRoute
        }

        test("getLatestLocationFromLastRouteSlice should get latest location from latest route slice") {
            val id = Activity.Id("1")
            activityDataSource.insertEmptyRouteSlice(id, start = Instant.fromEpochMilliseconds(0))
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
            activityDataSource.insertEmptyRouteSlice(id, Instant.fromEpochMilliseconds(0))
            firstSliceLocations.forEach {
                activityDataSource.insertLocationToLatestRouteSlice(id, it)
            }
            activityDataSource.insertEmptyRouteSlice(id, Instant.fromEpochMilliseconds(100))
            secondsSliceLocations.forEach {
                activityDataSource.insertLocationToLatestRouteSlice(id, it)
            }
            activityDataSource.getLatestLocationFromLastRouteSlice(id) shouldBe expectedLocation
        }

        test("getLatestLocationFromLastRouteSlice should return null if no location was found in latest route slice") {
            val id = Activity.Id("1")
            activityDataSource.insertEmptyRouteSlice(id, start = Instant.fromEpochMilliseconds(0))
            val firstSliceLocations = listOf(
                stubLocation(Instant.fromEpochMilliseconds(0)),
                stubLocation(Instant.fromEpochMilliseconds(10)),
                stubLocation(Instant.fromEpochMilliseconds(2000)),
            )
            activityDataSource.insertEmptyRouteSlice(id, Instant.fromEpochMilliseconds(0))
            firstSliceLocations.forEach {
                activityDataSource.insertLocationToLatestRouteSlice(id, it)
            }
            activityDataSource.insertEmptyRouteSlice(id, Instant.fromEpochMilliseconds(100))
            activityDataSource.getLatestLocationFromLastRouteSlice(id).shouldBeNull()
        }

        test("getLatestLocationFromLastRouteSlice should return null if no RouteSlices were found") {
            activityDataSource.getLatestLocationFromLastRouteSlice(Activity.Id("1")).shouldBeNull()
        }
    }
})
