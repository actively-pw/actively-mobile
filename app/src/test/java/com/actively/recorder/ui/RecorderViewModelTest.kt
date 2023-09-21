package com.actively.recorder.ui

import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.recorder.RecorderState
import com.actively.recorder.usecase.PauseRecordingUseCase
import com.actively.recorder.usecase.RecordingControlUseCases
import com.actively.recorder.usecase.ResumeRecordingUseCase
import com.actively.recorder.usecase.StartRecordingUseCase
import com.actively.recorder.usecase.StopRecordingUseCase
import com.actively.repository.ActivityRecordingRepository
import com.actively.stubs.stubActivityStats
import com.actively.stubs.stubRoute
import com.actively.util.TimeProvider
import com.mapbox.geojson.LineString
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class RecorderViewModelTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerTest
    val startRecordingUseCase = mockk<StartRecordingUseCase>(relaxUnitFun = true)
    val pauseRecordingUseCase = mockk<PauseRecordingUseCase>(relaxUnitFun = true)
    val resumeRecordingUseCase = mockk<ResumeRecordingUseCase>(relaxUnitFun = true)
    val stopRecordingUseCase = mockk<StopRecordingUseCase>(relaxUnitFun = true)
    val useCases = RecordingControlUseCases(
        startRecording = startRecordingUseCase,
        pauseRecording = pauseRecordingUseCase,
        resumeRecording = resumeRecordingUseCase,
        stopRecording = stopRecordingUseCase,
    )
    val timeProvider = mockk<TimeProvider>()
    val repository = mockk<ActivityRecordingRepository>(relaxed = true)


    context("initial states") {
        every { timeProvider.invoke() } returns Instant.fromEpochMilliseconds(0)
        every { repository.getRoute() } returns flowOf(stubRoute())
        every { repository.getStats() } returns flowOf(stubActivityStats(), stubActivityStats())
        every { repository.getState() } returns flowOf(RecorderState.Idle)
        val recorderViewModel = RecorderViewModel(useCases, timeProvider, repository)

        test("Calls repository to get flow of route") {
            verify(exactly = 1) { repository.getRoute() }
        }

        test("Properly sets route state") {
            val routeLocations = stubRoute().flatMap(RouteSlice::locations).map(Location::toPoint)
            val expectedJson = LineString.fromLngLats(routeLocations).toJson()
            recorderViewModel.route.value shouldBe expectedJson
        }

        test("Calls repository to get flow of recording state") {
            verify(exactly = 2) { repository.getState() }
        }

        test("Properly sets recorder state") {
            val expectedState = ControlsState(
                current = RecorderState.Idle,
                previous = RecorderState.Idle
            )
            recorderViewModel.controlsState.value shouldBe expectedState
        }

        test("Should resume recording if current recorder state is Started") {
            every { repository.getState() } returns flowOf(RecorderState.Started)
            RecorderViewModel(useCases, timeProvider, repository)
            coVerify(exactly = 1) {
                useCases.resumeRecording(any())
            }
        }

        test("Should launch stats updates if current recorder state is Started") {
            val mockRepository = mockk<ActivityRecordingRepository>(relaxed = true)
            every { mockRepository.getRoute() } returns flowOf(stubRoute())
            every { mockRepository.getState() } returns flowOf(RecorderState.Started)
            every { mockRepository.getStats() } returns flowOf(
                stubActivityStats(),
                stubActivityStats()
            )
            RecorderViewModel(useCases, timeProvider, mockRepository)
            verify(exactly = 2) { mockRepository.getStats() }
        }

        test("Should not resume recording if current recorder state is not Started") {
            coVerify(exactly = 0) { useCases.resumeRecording(any()) }
        }

        test("Calls repository to get stats") {
            verify(exactly = 1) { repository.getStats() }
        }

        test("Properly sets stats") {
            val expectedStats = StatisticsState(
                averageSpeed = "20.00",
                distance = "20.00",
                totalTime = "01:00:00"
            )
            recorderViewModel.stats.value shouldBe expectedStats
        }
    }

    context("Interactions with view model") {
        every { timeProvider.invoke() } returns Instant.fromEpochMilliseconds(0)
        every { repository.getRoute() } returns flowOf(stubRoute())
        every { repository.getStats() } returns flowOf(stubActivityStats(), stubActivityStats())
        every { repository.getState() } returns flowOf(RecorderState.Idle)
        val viewModel = RecorderViewModel(useCases, timeProvider, repository)

        test("Start recording calls StartRecordingUseCase") {
            viewModel.startRecording()
            coVerify(exactly = 1) {
                useCases.startRecording("Cycling", Instant.fromEpochMilliseconds(0))
            }
        }

        test("Start recording calls repository to launch stats updates") {
            viewModel.startRecording()
            verify(exactly = 2) { repository.getStats() }
        }

        test("Pause recording calls PauseRecordingUseCase") {
            viewModel.pauseRecording()
            verify(exactly = 1) { useCases.pauseRecording() }
        }

        test("Resume recording calls ResumeRecordingUseCase") {
            viewModel.resumeRecording()
            coVerify(exactly = 1) { useCases.resumeRecording(Instant.fromEpochMilliseconds(0)) }
        }

        test("Resume recording calls repository to launch stats updates") {
            viewModel.resumeRecording()
            verify(exactly = 2) { repository.getStats() }
        }
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }
})
