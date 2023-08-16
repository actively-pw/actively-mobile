package com.actively.datasource

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.stubs.stubActivity
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class ActivityDataSourceTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerTest

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    val database = ActivityDatabase(driver).apply {
        ActivityDatabase.Schema.create(driver)
    }

    context("ActivityDataSourceTest") {
        val activityDataSource = ActivityDataSourceImpl(database, testCoroutineScheduler)

        test("Should retrieve list of activities ordered from most recent") {
            val expectedActivities = listOf(
                stubActivity(id = "3", start = Instant.fromEpochMilliseconds(20)),
                stubActivity(id = "2", start = Instant.fromEpochMilliseconds(10)),
                stubActivity(id = "1", start = Instant.fromEpochMilliseconds(0))
            )
            expectedActivities.reversed().forEach {
                activityDataSource.insertActivity(it)
            }
            activityDataSource.getActivities().first() shouldContainInOrder expectedActivities
        }

        test("Should retrieve empty list of activities if none were found") {
            activityDataSource.getActivities().first().shouldBeEmpty()
        }

        test("Should insert and retrieve activity properly") {
            val activity = stubActivity(id = "1")
            activityDataSource.insertActivity(activity)
            activityDataSource.getActivity(id = activity.id) shouldBe activity
        }

        test("Should return null Activity if none were found") {
            activityDataSource.getActivity(id = Activity.Id("1")).shouldBeNull()
        }

        test("Should insert and retrieve activity stats properly") {
            val stats = stubActivityStats()
            activityDataSource.insertStats(stats = stats, id = Activity.Id("1"))
            activityDataSource.getStats(id = Activity.Id("1")).first() shouldBe stats
        }

        test("Should throw exception if none Activity.Stats were found") {
            shouldThrow<Exception> {
                activityDataSource.getStats(id = Activity.Id("1")).first()
            }
        }

        test("Should insert and retrieve route properly with locations ordered from the oldest") {
            val expectedRoute = listOf(
                stubLocation(timestamp = Instant.fromEpochMilliseconds(10)),
                stubLocation(timestamp = Instant.fromEpochMilliseconds(20)),
                stubLocation(timestamp = Instant.fromEpochMilliseconds(30)),
                stubLocation(timestamp = Instant.fromEpochMilliseconds(40)),
            )
            activityDataSource.insertRoute(route = expectedRoute.reversed(), id = Activity.Id("1"))
            activityDataSource.getRoute(id = Activity.Id("1")).first() shouldBe expectedRoute
        }

        test("Should return empty route if none were found") {
            activityDataSource.getRoute(id = Activity.Id("1")).first().shouldBeEmpty()
        }

        test("Should return latest route Location") {
            val expectedLocation = stubLocation(timestamp = Instant.fromEpochMilliseconds(400))
            val route = listOf(
                stubLocation(timestamp = Instant.fromEpochMilliseconds(20)),
                stubLocation(timestamp = Instant.fromEpochMilliseconds(10)),
                expectedLocation,
                stubLocation(timestamp = Instant.fromEpochMilliseconds(30)),
            )
            activityDataSource.insertRoute(route = route, id = Activity.Id("1"))
            activityDataSource.getLatestLocation(id = Activity.Id("1")) shouldBe expectedLocation
        }

        test("Should return null Location if route was empty or not found") {
            activityDataSource.getLatestLocation(id = Activity.Id("1")).shouldBeNull()
        }
    }
})
