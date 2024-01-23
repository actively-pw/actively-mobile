package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.distance.Distance.Companion.inWholeMeters
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.location.LocationProvider
import com.actively.repository.ActivityRecordingRepository
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubLocation
import com.actively.util.TimeProvider
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class RecordActivityUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    coroutineTestScope = true
    val locationProvider = mockk<LocationProvider>()
    val activityRecordingRepository =
        mockk<ActivityRecordingRepository>(relaxUnitFun = true)
    val timeProvider = mockk<TimeProvider>()
    val recordActivityUseCase = RecordActivityUseCaseImpl(
        locationProvider,
        activityRecordingRepository,
        timeProvider,
    )

    context("First update") {
        val start = Instant.fromEpochMilliseconds(0)
        every { timeProvider.invoke() } returns Instant.fromEpochMilliseconds(1000)
        val latestLocation = stubLocation()
        every {
            locationProvider.userLocation(6.seconds, 3.5.seconds, 6.meters)
        } returns flowOf(latestLocation)
        coEvery { activityRecordingRepository.updateStats(any()) } coAnswers { Activity.Stats.empty() }
        coEvery { activityRecordingRepository.getLatestRouteLocation() } returns null
        coEvery { activityRecordingRepository.getStats() } returns flowOf(Activity.Stats.empty())

        test("totalActivityTimeFlow should add time that passed since last stats update to total time of activity") {
            val transformLambdas = mutableListOf<(Activity.Stats) -> Activity.Stats>()
            coEvery {
                activityRecordingRepository.updateStats(capture(transformLambdas))
            } coAnswers {
                transformLambdas.first().invoke(Activity.Stats.empty())
            }
            recordActivityUseCase(start).first()
            coVerify { activityRecordingRepository.updateStats(any()) }
            transformLambdas.first().invoke(Activity.Stats.empty()) shouldBe Activity.Stats
                .empty().copy(totalTime = 1.seconds)
        }

        test("Should insert stats without distance and avg speed change") {
            val transformLambdas = mutableListOf<(Activity.Stats) -> Activity.Stats>()
            val currentStats = stubActivityStats(
                totalTime = 1.seconds,
                distance = 0.meters,
                averageSpeed = 0.0
            )
            coEvery {
                activityRecordingRepository.updateStats(capture(transformLambdas))
            } coAnswers {
                transformLambdas.first().invoke(currentStats)
            }
            recordActivityUseCase(start).first()
            transformLambdas[1].invoke(currentStats) shouldBe currentStats
        }

        test("totalDistanceFlow should call ActivityRepository to insert latest location") {
            recordActivityUseCase(start).first()
            coVerify(exactly = 1) {
                activityRecordingRepository.insertLocation(latestLocation)
            }
        }

        test("Should return current stats") {
            val transformLambdas = mutableListOf<(Activity.Stats) -> Activity.Stats>()
            coEvery {
                activityRecordingRepository.updateStats(capture(transformLambdas))
            } coAnswers {
                transformLambdas.first().invoke(Activity.Stats.empty())
            }
            val expectedStats = stubActivityStats(
                totalTime = 1.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            recordActivityUseCase(start).first() shouldBe expectedStats
        }
    }

    context("Consecutive updates") {
        val start = Instant.fromEpochMilliseconds(0)
        val previousLocation = stubLocation()
        val latestLocation = stubLocation(latitude = 0.001, longitude = 0.001)
        every { timeProvider.invoke() } returns Instant.fromEpochMilliseconds(2000)
        every {
            locationProvider.userLocation(6.seconds, 3.5.seconds, 6.meters)
        } returns flowOf(latestLocation)
        coEvery { activityRecordingRepository.getLatestRouteLocation() } returns previousLocation
        coEvery { activityRecordingRepository.getStats() } returns flowOf(
            stubActivityStats(totalTime = 10.seconds)
        )
        coEvery { activityRecordingRepository.updateStats(any()) } returns stubActivityStats()


        test("Should correctly update time") {
            val transformLambdas = mutableListOf<(Activity.Stats) -> Activity.Stats>()
            coEvery {
                activityRecordingRepository.updateStats(capture(transformLambdas))
            } coAnswers {
                transformLambdas.first().invoke(stubActivityStats(totalTime = 12.seconds))
            }
            recordActivityUseCase(start).first()
            transformLambdas.first()
                .invoke(stubActivityStats(totalTime = 10.seconds)) shouldBe stubActivityStats(
                totalTime = 12.seconds
            )
        }

        test("Should call ActivityRepository to insert latest location") {
            recordActivityUseCase(start).first()
            coVerify(exactly = 1) {
                activityRecordingRepository.insertLocation(latestLocation)
            }
        }

        test("Should return updated stats") {
            val transformLambdas = slot<(Activity.Stats) -> Activity.Stats>()
            val stats = stubActivityStats(
                totalTime = 2.seconds,
                distance = 0.meters,
                averageSpeed = 0.0
            )
            coEvery {
                activityRecordingRepository.getStats()
            } returns flowOf(stats)
            coEvery {
                activityRecordingRepository.updateStats(capture(transformLambdas))
            } coAnswers {
                transformLambdas.captured(stats)
            }
            val actual = recordActivityUseCase(start).first()
            actual.totalTime shouldBe 2.seconds
            actual.distance.inWholeMeters shouldBe 157
            actual.averageSpeed.toInt() shouldBe 283
        }
    }
})
