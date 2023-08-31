package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk

class SetRecorderStateUseCaseTest : FunSpec({

    val activityRecordingRepository = mockk<ActivityRecordingRepository>(relaxUnitFun = true)
    val setRecorderStateUseCase = SetRecorderStateUseCaseImpl(activityRecordingRepository)

    test("Should set recorder state") {
        setRecorderStateUseCase(RecorderState.Stopped)
        coVerify(exactly = 1) { activityRecordingRepository.setState(RecorderState.Stopped) }
    }
})
