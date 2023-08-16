package com.actively.recorder.usecase

import com.actively.activity.Activity
import com.actively.activity.usecase.GetStatsUseCase
import com.actively.activity.usecase.InsertStatsUseCase
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.location.usecase.GetLastRouteLocationUseCase
import com.actively.location.usecase.GetUserLocationUpdatesUseCase
import com.actively.location.usecase.InsertLocationUseCase
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
    val getUserLocationUpdatesUseCase = mockk<GetUserLocationUpdatesUseCase>()
    val getLastRouteLocationUseCase = mockk<GetLastRouteLocationUseCase>()
    val getStatsUseCase = mockk<GetStatsUseCase>()
    val insertStatsUseCase = mockk<InsertStatsUseCase>(relaxUnitFun = true)
    val insertLocationUseCase = mockk<InsertLocationUseCase>(relaxUnitFun = true)
    val recordActivityUseCase = RecordActivityUseCaseImpl(
        getUserLocationUpdatesUseCase,
        getLastRouteLocationUseCase,
        getStatsUseCase,
        insertStatsUseCase,
        insertLocationUseCase
    )

    context("First location update") {
        val activityId = Activity.Id("1")
        val start = Instant.fromEpochMilliseconds(0)
        val latestLocation = stubLocation(timestamp = Instant.fromEpochMilliseconds(2000))
        every { getUserLocationUpdatesUseCase() } returns flowOf(latestLocation)
        coEvery { getLastRouteLocationUseCase(any()) } returns null
        coEvery { getStatsUseCase(id = activityId) } returns flowOf(Activity.Stats.empty())

        test("Should call GetLatestRouteLocationUseCase to get previous user location") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { getLastRouteLocationUseCase(activityId) }
        }

        test("Should call GetStatsUseCase to get previous stats") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { getStatsUseCase(activityId) }
        }

        test("Should call InsertStatsUseCase with correctly updated stats") {
            recordActivityUseCase(activityId, start).first()
            val expectedStats = stubActivityStats(
                totalTime = 2.seconds,
                distance = 0.kilometers,
                averageSpeed = 0.0
            )
            coVerify(exactly = 1) { insertStatsUseCase(expectedStats, activityId) }
        }

        test("Should call InsertLocationUseCase to insert latest location") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { insertLocationUseCase(latestLocation, activityId) }
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
            coEvery { getStatsUseCase(activityId) } returns emptyFlow()
            recordActivityUseCase(activityId, start).firstOrNull().shouldBeNull()
        }

        test("Should not insert updated stats if GetStatsUseCase returned empty flow") {
            coEvery { getStatsUseCase(activityId) } returns emptyFlow()
            recordActivityUseCase(activityId, start).firstOrNull()
            coVerify(exactly = 0) { insertStatsUseCase(any(), any()) }
        }

        test("Should not insert latest location if GetStatsUseCase returned empty flow") {
            coEvery { getStatsUseCase(activityId) } returns emptyFlow()
            recordActivityUseCase(activityId, start).firstOrNull()
            coVerify(exactly = 0) { insertLocationUseCase(any(), any()) }
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
        every { getUserLocationUpdatesUseCase() } returns flowOf(latestLocation)
        coEvery { getLastRouteLocationUseCase(any()) } returns previousLocation
        coEvery { getStatsUseCase(id = activityId) } returns flowOf(Activity.Stats.empty())

        test("Should correctly update stats") {
            recordActivityUseCase(activityId, start).first()
            val expectedStats = Activity.Stats(
                totalTime = 2.seconds,
                distance = 157.meters,
                averageSpeed = 282.6
            )
            coVerify(exactly = 1) { insertStatsUseCase(expectedStats, activityId) }
        }

        test("Should call InsertLocationUseCase to insert latest location") {
            recordActivityUseCase(activityId, start).first()
            coVerify(exactly = 1) { insertLocationUseCase(latestLocation, activityId) }
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
