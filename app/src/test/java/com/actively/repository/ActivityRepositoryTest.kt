package com.actively.repository

import com.actively.activity.Activity
import com.actively.datasource.ActivityDataSource
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

class ActivityRepositoryTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerLeaf

    val activityDataSource = mockk<ActivityDataSource>(relaxUnitFun = true)


    context("getActivities()") {
        val repository = ActivityRepositoryImpl(activityDataSource)
        val activities = listOf(stubActivity(id = "1"), stubActivity(id = "2"))
        every { activityDataSource.getActivities() } returns flowOf(activities)

        test("Should return flow with list of activities") {
            repository.getActivities().first() shouldBe activities
        }

        test("Should call ActivityDataSource getActivities()") {
            repository.getActivities()
            verify(exactly = 1) { activityDataSource.getActivities() }
        }
    }

    context("getActivity(id: Activity.Id)") {
        val repository = ActivityRepositoryImpl(activityDataSource)
        coEvery { activityDataSource.getActivity(any()) } returns null
        coEvery { activityDataSource.getActivity(id = Activity.Id("1")) } returns stubActivity(id = "1")

        test("Should return activity with given id") {
            forAll(
                row(Activity.Id("1"), stubActivity(id = "1")),
                row(Activity.Id("non-existing-id"), null)
            ) { id, activity ->
                repository.getActivity(id) shouldBe activity
            }
        }

        test("Should call ActivityDataSource getActivity with proper id") {
            forAll(
                row(Activity.Id("1")),
                row(Activity.Id("non-existing-id")),
            ) { id ->
                repository.getActivity(id)
                coVerify(exactly = 1) { activityDataSource.getActivity(id) }
            }
        }
    }

    context("getStats(id: Activity.Id)") {
        val repository = ActivityRepositoryImpl(activityDataSource)
        every { activityDataSource.getStats(any()) } returns flow { throw IllegalArgumentException() }
        every { activityDataSource.getStats(id = Activity.Id("1")) } returns flowOf(
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

        test("Should call ActivityDataSource getStats with proper id") {
            forAll(
                row(Activity.Id("1")),
                row(Activity.Id("non-existing-id"))
            ) { id ->
                repository.getStats(id)
                verify(exactly = 1) { activityDataSource.getStats(id) }
            }
        }
    }

    context("insert*()") {
        val repository = ActivityRepositoryImpl(activityDataSource)

        test("Should call ActivityDataSource insertActivity with given activity") {
            val activity = stubActivity()
            repository.insertActivity(activity)
            coVerify(exactly = 1) { activityDataSource.insertActivity(activity) }
        }

        test("Should call ActivityDataSource insertStats with given stats and id") {
            val stats = stubActivityStats()
            val id = Activity.Id("1")
            repository.insertStats(stats, id)
            coVerify(exactly = 1) { activityDataSource.insertStats(stats, id) }
        }

        test("Should call ActivityDataSource insertLocation with given location and id") {
            val location = stubLocation()
            val id = Activity.Id("1")
            repository.insertLocation(location, id)
            coVerify(exactly = 1) { activityDataSource.insertLocation(location, id) }
        }
    }

    context("get route methods") {
        val repository = ActivityRepositoryImpl(activityDataSource)
        val id = Activity.Id("1")
        every { activityDataSource.getRoute(id) } returns flowOf(stubRoute())
        coEvery { activityDataSource.getLatestLocation(id) } returns stubLocation()

        test("Should call ActivityDataSource getRoute with given activity id") {
            repository.getRoute(id).collect()
            coVerify(exactly = 1) { activityDataSource.getRoute(id) }
        }

        test("Should return route from ActivityDataSource") {
            val expectedRoute = stubRoute()
            expectedRoute.forEach { repository.insertLocation(it, id) }
            repository.getRoute(id).first() shouldBe expectedRoute
        }

        test("Should call ActivityDataSource to get latest route location") {
            repository.getLatestRouteLocation(id)
            coVerify(exactly = 1) { activityDataSource.getLatestLocation(id) }
        }

        test("Should return latest location from route") {
            repository.getLatestRouteLocation(id) shouldBe stubLocation()
        }
    }
})
