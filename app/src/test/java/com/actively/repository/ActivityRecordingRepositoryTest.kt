package com.actively.repository

import com.actively.activity.Activity
import com.actively.datasource.ActivityRecordingDataSource
import com.actively.datasource.SyncActivitiesDataSource
import com.actively.recorder.RecorderState
import com.actively.stubs.stubActivity
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import com.actively.stubs.stubRouteSlice
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant

class ActivityRecordingRepositoryTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerLeaf

    val activityRecordingDataSource = mockk<ActivityRecordingDataSource>(relaxUnitFun = true)
    val syncActivitiesDataSource = mockk<SyncActivitiesDataSource>(relaxUnitFun = true)

    context("getActivity()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)
        coEvery { activityRecordingDataSource.getActivity(id = Activity.Id("1")) } returns stubActivity(
            id = "1"
        )

        test("Should return activity with given id") {
            repository.getActivity(id = Activity.Id("1")) shouldBe stubActivity(id = "1")
        }

        test("Should return null if no Activity was found") {
            coEvery { activityRecordingDataSource.getActivity(id = Activity.Id("1")) } returns null
            repository.getActivity(id = Activity.Id("1")).shouldBeNull()
        }

        test("Should call ActivityRecordingDataSource getActivity") {
            repository.getActivity(id = Activity.Id("1"))
            coVerify(exactly = 1) { activityRecordingDataSource.getActivity(id = Activity.Id("1")) }
        }
    }

    context("isActivityPresent()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)
        coEvery { activityRecordingDataSource.getActivityCount() } returns 1

        test("Should return true if activity is present in database") {
            repository.isActivityPresent().shouldBeTrue()
        }

        test("Should return false if activity is not present in database") {
            coEvery { activityRecordingDataSource.getActivityCount() } returns 0
            repository.isActivityPresent().shouldBeFalse()
        }

        test("Should call ActivityRecordingDataSource to get count of activities") {
            repository.isActivityPresent()
            coVerify(exactly = 1) { activityRecordingDataSource.getActivityCount() }
        }
    }

    context("getStats()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)
        every { activityRecordingDataSource.getStats() } returns flowOf(stubActivityStats())

        test("Should return Stats for activity") {
            repository.getStats().first() shouldBe stubActivityStats()
        }

        test("Should call ActivityRecordingDataSource getStats") {
            repository.getStats()
            verify(exactly = 1) { activityRecordingDataSource.getStats() }
        }
    }

    context("getRoute()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)
        val expectedRoute = listOf(
            stubRouteSlice(start = Instant.fromEpochMilliseconds(0)),
            stubRouteSlice(start = Instant.fromEpochMilliseconds(100)),
            stubRouteSlice(start = Instant.fromEpochMilliseconds(200)),
        )
        every { activityRecordingDataSource.getRoute() } returns flowOf(expectedRoute)

        test("Should call ActivityRecordingDataSource with given activity id") {
            repository.getRoute()
            coVerify(exactly = 1) {
                activityRecordingDataSource.getRoute()
            }
        }

        test("Should return route from ActivityRecordingDataSource") {
            repository.getRoute().first() shouldBe expectedRoute
        }
    }

    context("getLatestRouteLocation()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)
        coEvery { activityRecordingDataSource.getLatestLocationFromLastRouteSlice() } returns null
        coEvery { activityRecordingDataSource.getLatestLocationFromLastRouteSlice() } returns stubLocation()

        test("Should call ActivityRecordingDataSource with given activity id") {
            repository.getLatestRouteLocation()
            coVerify(exactly = 1) {
                activityRecordingDataSource.getLatestLocationFromLastRouteSlice()
            }
        }

        test("Should return route from ActivityRecordingDataSource") {
            repository.getLatestRouteLocation() shouldBe stubLocation()
        }
    }

    context("insertRoutelessActivity()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)

        test("Should call ActivityRecordingDataSource") {
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
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)

        test("Should call ActivityRecordingDataSource") {
            val stats = stubActivityStats()
            repository.insertStats(stats)
            coVerify(exactly = 1) {
                activityRecordingDataSource.insertStats(stats)
            }
        }
    }

    context("insertEmptyRouteSlice()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)

        test("Should call ActivityRecordingDataSource") {
            val timestamp = Instant.fromEpochMilliseconds(0)
            repository.insertEmptyRouteSlice(timestamp)
            coVerify(exactly = 1) {
                activityRecordingDataSource.insertEmptyRouteSlice(timestamp)
            }
        }
    }

    context("insertLocation()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)

        test("Should call ActivityRecordingDataSource") {
            val location = stubLocation()
            repository.insertLocation(location)
            coVerify(exactly = 1) {
                activityRecordingDataSource.insertLocationToLatestRouteSlice(location)
            }
        }
    }

    context("getState()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)
        every { activityRecordingDataSource.getState() } returns flowOf(RecorderState.Idle)

        test("Should return RecordingState from ActivityRecordingDataSource") {
            repository.getState().first() shouldBe RecorderState.Idle
        }

        test("Should call ActivityRecordingDataSource") {
            repository.getState()
            verify(exactly = 1) { activityRecordingDataSource.getState() }
        }
    }

    context("setState()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)

        test("Should call ActivityRecordingDataSource") {
            repository.setState(RecorderState.Idle)
            coVerify(exactly = 1) { activityRecordingDataSource.setState(RecorderState.Idle) }
        }
    }

    context("updateStats()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)
        coEvery { activityRecordingDataSource.updateStats(any()) } returns stubActivityStats()

        test("Should call ActivityRecordingDataSource with provided lambda function") {
            val transform: (Activity.Stats) -> Activity.Stats = { it }
            repository.updateStats(transform)
            coVerify(exactly = 1) { activityRecordingDataSource.updateStats(transform) }
        }

        test("Should return updated stats returned from data source") {
            repository.updateStats { it } shouldBe stubActivityStats()
        }
    }

    context("syncActivity()") {
        val repository =
            ActivityRecordingRepositoryImpl(activityRecordingDataSource, syncActivitiesDataSource)

        test("Should call SyncActivitiesRepositoryDataSource") {
            repository.syncActivity(stubActivity())
            coVerify(exactly = 1) { syncActivitiesDataSource.syncActivity(stubActivity()) }
        }
    }
})
