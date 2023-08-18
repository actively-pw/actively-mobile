package com.actively.repository

import com.actively.activity.Activity
import com.actively.datasource.ActivityRecordingDataSource
import com.actively.stubs.stubActivity
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import com.actively.stubs.stubRoute
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

class ActivityRecordingRepositoryTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerLeaf

    val activityRecordingDataSource = mockk<ActivityRecordingDataSource>(relaxUnitFun = true)


    context("getActivities()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)
        val activities = listOf(stubActivity(id = "1"), stubActivity(id = "2"))
        every { activityRecordingDataSource.getActivities() } returns flowOf(activities)

        test("Should return flow with list of activities") {
            repository.getActivities().first() shouldBe activities
        }

        test("Should call ActivityRecordingDataSource getActivities()") {
            repository.getActivities()
            verify(exactly = 1) { activityRecordingDataSource.getActivities() }
        }
    }

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

    context("insert*()") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)

        test("Should call ActivityRecordingDataSource insertActivity with given activity") {
            val activity = stubActivity()
            repository.insertActivity(activity)
            coVerify(exactly = 1) { activityRecordingDataSource.insertActivity(activity) }
        }

        test("Should call ActivityRecordingDataSource insertStats with given stats and id") {
            val stats = stubActivityStats()
            val id = Activity.Id("1")
            repository.insertStats(stats, id)
            coVerify(exactly = 1) { activityRecordingDataSource.insertStats(stats, id) }
        }

        test("Should call ActivityRecordingDataSource insertLocation with given location and id") {
            val location = stubLocation()
            val id = Activity.Id("1")
            repository.insertLocation(location, id)
            coVerify(exactly = 1) { activityRecordingDataSource.insertLocation(location, id) }
        }
    }

    context("get route methods") {
        val repository = ActivityRecordingRepositoryImpl(activityRecordingDataSource)
        val id = Activity.Id("1")
        every { activityRecordingDataSource.getRoute(id) } returns flowOf(stubRoute())
        coEvery { activityRecordingDataSource.getLatestLocation(id) } returns stubLocation()

        test("Should call ActivityRecordingDataSource getRoute with given activity id") {
            repository.getRoute(id).collect()
            coVerify(exactly = 1) { activityRecordingDataSource.getRoute(id) }
        }

        test("Should return route from ActivityRecordingDataSource") {
            val expectedRoute = stubRoute()
            expectedRoute.forEach { repository.insertLocation(it, id) }
            repository.getRoute(id).first() shouldBe expectedRoute
        }

        test("Should call ActivityRecordingDataSource to get latest route location") {
            repository.getLatestRouteLocation(id)
            coVerify(exactly = 1) { activityRecordingDataSource.getLatestLocation(id) }
        }

        test("Should return latest location from route") {
            repository.getLatestRouteLocation(id) shouldBe stubLocation()
        }
    }
})
