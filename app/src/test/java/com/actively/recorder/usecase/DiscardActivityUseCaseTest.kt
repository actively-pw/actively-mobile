package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk

class DiscardActivityUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val repository = mockk<ActivityRecordingRepository>(relaxed = true)
    val useCase = DiscardActivityUseCaseImpl(repository)

    test("Should set recorder state do Idle") {
        useCase()
        coVerify(exactly = 1) { repository.setState(RecorderState.Idle) }
    }

    test("Should remove recorded activity") {
        useCase()
        coVerify(exactly = 1) { repository.removeRecordingActivity() }
    }
})
