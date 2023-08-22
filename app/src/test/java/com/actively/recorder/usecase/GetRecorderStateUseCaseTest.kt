package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flowOf

class GetRecorderStateUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val activityRecordingRepository = mockk<ActivityRecordingRepository>()
    val getRecorderStateUseCase = GetRecorderStateUseCaseImpl(activityRecordingRepository)

    test("Should return flow of states from repository") {
        val expectedStates = listOf(
            RecorderState.Idle,
            RecorderState.Started,
            RecorderState.Paused,
            RecorderState.Paused
        )
        every { activityRecordingRepository.getState() } returns expectedStates.asFlow()
        getRecorderStateUseCase().collectIndexed { index, state ->
            state shouldBe expectedStates[index]
        }
    }

    test("Should call repository to get state") {
        every { activityRecordingRepository.getState() } returns flowOf(RecorderState.Idle)
        getRecorderStateUseCase().collect()
        coVerify(exactly = 1) { activityRecordingRepository.getState() }
    }
})
