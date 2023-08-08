package com.actively.repository

import com.actively.activity.Activity
import com.actively.datasource.ActivityDataSource
import com.actively.stubs.stubActivity
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class ActivityRepositoryTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerLeaf

    val activityDataSource = mockk<ActivityDataSource>(relaxUnitFun = true)


    context("getActivities()") {
        val repository = ActivityRepositoryImpl(activityDataSource, testCoroutineScheduler)
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
        val repository = ActivityRepositoryImpl(activityDataSource, testCoroutineScheduler)
        every { activityDataSource.getActivity(any()) } returns null
        every { activityDataSource.getActivity(id = Activity.Id("1")) } returns stubActivity(id = "1")

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
                verify(exactly = 1) { activityDataSource.getActivity(id) }
            }
        }
    }

    context("getStats(id: Activity.Id)") {
        val repository = ActivityRepositoryImpl(activityDataSource, testCoroutineScheduler)
        every { activityDataSource.getStats(any()) } returns null
        every { activityDataSource.getStats(id = Activity.Id("1")) } returns stubActivityStats()

        test("Should return Stats for activity given by id") {
            forAll(
                row(Activity.Id("1"), stubActivityStats()),
                row(Activity.Id("non-existing-id"), null)
            ) { id, stats ->
                repository.getStats(id) shouldBe stats
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
        val repository = ActivityRepositoryImpl(activityDataSource, testCoroutineScheduler)

        test("Should call ActivityDataSource insertActivity with given activity") {
            val activity = stubActivity()
            repository.insertActivity(activity)
            verify(exactly = 1) { activityDataSource.insertActivity(activity) }
        }

        test("Should call ActivityDataSource insertStats with given stats and id") {
            val stats = stubActivityStats()
            val id = Activity.Id("1")
            repository.insertStats(stats, id)
            verify(exactly = 1) { activityDataSource.insertStats(stats, id) }
        }

        test("Should call ActivityDataSource insertLocation with given location and id") {
            val location = stubLocation()
            val id = Activity.Id("1")
            repository.insertLocation(location, id)
            verify(exactly = 1) { activityDataSource.insertLocation(location, id) }
        }
    }
})
