package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.location.LocationProvider
import com.actively.repository.ActivityRecordingRepository
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
    val activityRecordingRepository = mockk<ActivityRecordingRepository>(relaxUnitFun = true)
    val recordActivityUseCase = RecordActivityUseCaseImpl(
        locationProvider,
        activityRecordingRepository
    )

    context("First location update") {
        val start = Instant.fromEpochMilliseconds(0)
        val latestLocation = stubLocation(timestamp = Instant.fromEpochMilliseconds(2000))
        every {
            locationProvider.userLocation(3.seconds, 1.seconds, 2.meters)
        } returns flowOf(latestLocation)
        coEvery { activityRecordingRepository.getLatestRouteLocation() } returns null
        coEvery { activityRecordingRepository.getStats() } returns flowOf(Activity.Stats.empty())

        test("Should call ActivityRepository to get previous user location") {
            recordActivityUseCase(start).first()
            coVerify(exactly = 1) { activityRecordingRepository.getLatestRouteLocation() }
        }

        test("Should call ActivityRepository to get previous activity stats") {
            recordActivityUseCase(start).first()
            coVerify(exactly = 1) { activityRecordingRepository.getStats() }
        }

        test("Should call ActivityRepository to insert correctly updated stats") {
            recordActivityUseCase(start).first()
            val expectedStats = stubActivityStats(
                totalTime = 2.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            coVerify(exactly = 1) {
                activityRecordingRepository.insertStats(expectedStats)
            }
        }

        test("Should properly calculate stats when elapsed time is negative") {
            recordActivityUseCase(Instant.fromEpochMilliseconds(3000)).first()
            val expectedStats = stubActivityStats(
                totalTime = 0.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            coVerify(exactly = 1) {
                activityRecordingRepository.insertStats(expectedStats)
            }
        }

        test("Should call ActivityRepository to insert latest location") {
            recordActivityUseCase(start).first()
            coVerify(exactly = 1) {
                activityRecordingRepository.insertLocation(latestLocation)
            }
        }

        test("Should return current stats") {
            val expectedStats = stubActivityStats(
                totalTime = 2.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            recordActivityUseCase(start).first() shouldBe expectedStats
        }

        test("Should return null if ActivityRepository returned empty flow of Activity Stats") {
            coEvery { activityRecordingRepository.getStats() } returns emptyFlow()
            recordActivityUseCase(start).firstOrNull().shouldBeNull()
        }

        test("Should not insert updated stats if ActivityRepository returned empty flow of Activity Stats") {
            coEvery { activityRecordingRepository.getStats() } returns emptyFlow()
            recordActivityUseCase(start).firstOrNull()
            coVerify(exactly = 0) { activityRecordingRepository.insertStats(any()) }
        }

        test("Should not insert latest location if ActivityRepository returned empty flow of Activity Stats") {
            coEvery { activityRecordingRepository.getStats() } returns emptyFlow()
            recordActivityUseCase(start).firstOrNull()
            coVerify(exactly = 0) { activityRecordingRepository.insertLocation(any()) }
        }
    }

    context("Two location update") {
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
        coEvery { activityRecordingRepository.getLatestRouteLocation() } returns previousLocation
        coEvery { activityRecordingRepository.getStats() } returns flowOf(Activity.Stats.empty())

        test("Should correctly update stats") {
            recordActivityUseCase(start).first()
            val expectedStats = Activity.Stats(
                totalTime = 2.seconds,
                distance = 157.meters,
                averageSpeed = 282.6
            )
            coVerify(exactly = 1) {
                activityRecordingRepository.insertStats(expectedStats)
            }
        }

        test("Should call ActivityRepository to insert latest location") {
            recordActivityUseCase(start).first()
            coVerify(exactly = 1) {
                activityRecordingRepository.insertLocation(latestLocation)
            }
        }

        test("Should return updated stats") {
            recordActivityUseCase(start).first()
            val expectedStats = Activity.Stats(
                totalTime = 2.seconds,
                distance = 157.meters,
                averageSpeed = 282.6
            )
            recordActivityUseCase(start).first() shouldBe expectedStats
        }
    }
})
