package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.location.LocationProvider
import com.actively.repository.ActivityRepository
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class RecordActivityUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val locationProvider = mockk<LocationProvider>()
    val activityRepository = mockk<ActivityRepository>(relaxUnitFun = true)
    val recordActivityUseCase = RecordActivityUseCaseImpl(locationProvider, activityRepository)

    context("First location update") {
        val activityId = Activity.Id("1")
        val start = Instant.fromEpochMilliseconds(0)
        val latestLocation = stubLocation(timestamp = Instant.fromEpochMilliseconds(2000))
        every {
            locationProvider.userLocation(3.seconds, 1.seconds, 2.meters)
        } returns flowOf(latestLocation)
        coEvery { activityRepository.getLatestRouteLocation(activityId) } returns null
        coEvery { activityRepository.getStats(activityId) } returns flowOf(Activity.Stats.empty())

        test("Should call GetLatestRouteLocationUseCase to get previous user location") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { activityRepository.getLatestRouteLocation(activityId) }
        }

        test("Should call GetStatsUseCase to get previous stats") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { activityRepository.getStats(activityId) }
        }

        test("Should call InsertStatsUseCase with correctly updated stats") {
            recordActivityUseCase(activityId, start).first()
            val expectedStats = stubActivityStats(
                totalTime = 2.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            coVerify(exactly = 1) { activityRepository.insertStats(expectedStats, activityId) }
        }

        test("Should properly calculate stats when elapsed time is negative") {
            recordActivityUseCase(activityId, Instant.fromEpochMilliseconds(3000)).first()
            val expectedStats = stubActivityStats(
                totalTime = 0.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            coVerify(exactly = 1) { activityRepository.insertStats(expectedStats, activityId) }
        }

        test("Should call InsertLocationUseCase to insert latest location") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { activityRepository.insertLocation(latestLocation, activityId) }
        }

        test("Should return current stats") {
            val expectedStats = stubActivityStats(
                totalTime = 2.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            recordActivityUseCase(activityId, start).first() shouldBe expectedStats
        }

        test("Should return null if GetStatsUseCase returned empty flow") {
            coEvery { activityRepository.getStats(activityId) } returns emptyFlow()
            recordActivityUseCase(activityId, start).firstOrNull().shouldBeNull()
        }

        test("Should not insert updated stats if GetStatsUseCase returned empty flow") {
            coEvery { activityRepository.getStats(activityId) } returns emptyFlow()
            recordActivityUseCase(activityId, start).firstOrNull()
            coVerify(exactly = 0) { activityRepository.insertStats(any(), any()) }
        }

        test("Should not insert latest location if GetStatsUseCase returned empty flow") {
            coEvery { activityRepository.getStats(activityId) } returns emptyFlow()
            recordActivityUseCase(activityId, start).firstOrNull()
            coVerify(exactly = 0) { activityRepository.insertLocation(any(), any()) }
        }
    }

    context("Consecutive location update") {
        val activityId = Activity.Id("1")
        val start = Instant.fromEpochMilliseconds(0)
        val previousLocation = stubLocation(timestamp = start)
        val latestLocation = stubLocation(
            timestamp = Instant.fromEpochMilliseconds(2000),
            latitude = 0.001,
            longitude = 0.001
        )
        every {
            locationProvider.userLocation(3.seconds, 1.seconds, 2.meters)
        } returns flowOf(latestLocation)
        coEvery { activityRepository.getLatestRouteLocation(any()) } returns previousLocation
        coEvery { activityRepository.getStats(id = activityId) } returns flowOf(Activity.Stats.empty())

        test("Should correctly update stats") {
            recordActivityUseCase(activityId, start).first()
            val expectedStats = Activity.Stats(
                totalTime = 2.seconds,
                distance = 157.meters,
                averageSpeed = 282.6
            )
            coVerify(exactly = 1) { activityRepository.insertStats(expectedStats, activityId) }
        }

        test("Should call InsertLocationUseCase to insert latest location") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { activityRepository.insertLocation(latestLocation, activityId) }
        }

        test("Should return updated stats") {
            recordActivityUseCase(activityId, start).first()
            val expectedStats = Activity.Stats(
                totalTime = 2.seconds,
                distance = 157.meters,
                averageSpeed = 282.6
            )
            recordActivityUseCase(activityId, start).first() shouldBe expectedStats
        }
    }
})
