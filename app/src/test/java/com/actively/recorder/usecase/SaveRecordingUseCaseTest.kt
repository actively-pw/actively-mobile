package com.actively.recorder.usecase

import com.actively.recorder.RecorderState
import com.actively.repository.ActivityRecordingRepository
import com.actively.synchronizer.usecases.LaunchSynchronizationUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

class SaveRecordingUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val stopRecordingUseCase = mockk<StopRecordingUseCase>(relaxUnitFun = true)
    val syncUseCase = mockk<LaunchSynchronizationUseCase>(relaxUnitFun = true)
    val repository = mockk<ActivityRecordingRepository>(relaxed = true)
    val saveRecordingUseCase = SaveActivityUseCaseImpl(
        launchSynchronizationUseCase = syncUseCase,
        activityRecordingRepository = repository,
        stopRecordingUseCase = stopRecordingUseCase
    )

    test("Should mark activity as recorded") {
        saveRecordingUseCase("Morning Activity")
        coVerify(exactly = 1) { repository.markActivityAsRecorded() }
    }

    test("Should update title of activity") {
        saveRecordingUseCase("Morning Activity")
        coVerify(exactly = 1) { repository.updateRecordingActivityTitle("Morning Activity") }
    }

    test("Should launch synchronization") {
        saveRecordingUseCase("Morning activity")
        verify(exactly = 1) { syncUseCase() }
    }

    test("Should set recorded state to Idle") {
        saveRecordingUseCase("Morning activity")
        coVerify(exactly = 1) { repository.setState(RecorderState.Idle) }
    }
})
