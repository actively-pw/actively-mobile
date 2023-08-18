package com.actively.repository

import com.actively.activity.Activity
import com.actively.datasource.ActivityRecordingDataSource
import com.actively.stubs.stubActivity
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import com.actively.stubs.stubRouteSlice
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant

class ActivityRecordingRepositoryTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerLeaf

    val activityRecordingDataSource = mockk<ActivityRecordingDataSource>(relaxUnitFun = true)

    context("getActivity(id: Activity.Id)") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)
        coEvery { activityRecordingDataSource.getActivity(any()) } returns null
        coEvery {
            activityRecordingDataSource.getActivity(id = Activity.Id("1"))
        } returns stubActivity(id = "1")

        test("Should return activity with given id") {
            forAll(
                row(Activity.Id("1"), stubActivity(id = "1")),
                row(Activity.Id("non-existing-id"), null)
            ) { id, activity ->
                repository.getActivity(id) shouldBe activity
            }
        }

        test("Should call ActivityRecordingDataSource getActivity with proper id") {
            forAll(
                row(Activity.Id("1")),
                row(Activity.Id("non-existing-id")),
            ) { id ->
                repository.getActivity(id)
                coVerify(exactly = 1) { activityRecordingDataSource.getActivity(id) }
            }
        }
    }

    context("getStats(id: Activity.Id)") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)
        every { activityRecordingDataSource.getStats(any()) } returns flow { throw IllegalArgumentException() }
        every { activityRecordingDataSource.getStats(id = Activity.Id("1")) } returns flowOf(
            stubActivityStats()
        )

        test("Should return Stats for activity given by id") {
            val expected = stubActivityStats()
            repository.getStats(id = Activity.Id("1")).first() shouldBe expected
        }

        test("Should throw exception for non non existing activity") {
            shouldThrow<IllegalArgumentException> {
                repository.getStats(id = Activity.Id("non-existing-activity")).collect()
            }
        }

        test("Should call ActivityRecordingDataSource getStats with proper id") {
            forAll(
                row(Activity.Id("1")),
                row(Activity.Id("non-existing-id"))
            ) { id ->
                repository.getStats(id)
                verify(exactly = 1) { activityRecordingDataSource.getStats(id) }
            }
        }
    }

    context("getRoute()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)
        val id = Activity.Id("1")
        val expectedRoute = listOf(
            stubRouteSlice(start = Instant.fromEpochMilliseconds(0)),
            stubRouteSlice(start = Instant.fromEpochMilliseconds(100)),
            stubRouteSlice(start = Instant.fromEpochMilliseconds(200)),
        )
        every { activityRecordingDataSource.getRoute(id) } returns flowOf(expectedRoute)

        test("Should call ActivityRecordingDataSource with given activity id") {
            repository.getRoute(id)
            coVerify(exactly = 1) {
                activityRecordingDataSource.getRoute(id)
            }
        }

        test("Should return route from ActivityRecordingDataSource") {
            repository.getRoute(id).first() shouldBe expectedRoute
        }
    }

    context("getLatestRouteLocation()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)
        val id = Activity.Id("1")
        coEvery { activityRecordingDataSource.getLatestLocationFromLastRouteSlice(any()) } returns null
        coEvery { activityRecordingDataSource.getLatestLocationFromLastRouteSlice(id) } returns stubLocation()

        test("Should call ActivityRecordingDataSource with given activity id") {
            repository.getLatestRouteLocation(id)
            coVerify(exactly = 1) {
                activityRecordingDataSource.getLatestLocationFromLastRouteSlice(id)
            }
        }

        test("Should return route from ActivityRecordingDataSource") {
            repository.getLatestRouteLocation(id) shouldBe stubLocation()
        }
    }

    context("insertRoutelessActivity()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)

        test("Should call ActivityRecordingRepositoryImpl") {
            val activity = stubActivity()
            repository.insertRoutelessActivity(activity)
            coVerify(exactly = 1) {
                activityRecordingDataSource.insertActivity(
                    activity.id,
                    activity.title,
                    activity.sport,
                    activity.stats
                )
            }
        }
    }

    context("insertStats()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)

        test("Should call ActivityRecordingRepositoryImpl") {
            val stats = stubActivityStats()
            repository.insertStats(stats, Activity.Id("1"))
            coVerify(exactly = 1) {
                activityRecordingDataSource.insertStats(stats, Activity.Id("1"))
            }
        }
    }

    context("insertEmptyRouteSlice()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)

        test("Should call ActivityRecordingRepositoryImpl") {
            val id = Activity.Id("1")
            val timestamp = Instant.fromEpochMilliseconds(0)
            repository.insertEmptyRouteSlice(id, timestamp)
            coVerify(exactly = 1) {
                activityRecordingDataSource.insertEmptyRouteSlice(id, timestamp)
            }
        }
    }

    context("insertLocation()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)

        test("Should call ActivityRecordingRepositoryImpl") {
            val id = Activity.Id("1")
            val location = stubLocation()
            repository.insertLocation(location, id)
            coVerify(exactly = 1) {
                activityRecordingDataSource.insertLocationToLatestRouteSlice(id, location)
            }
        }
    }
})
