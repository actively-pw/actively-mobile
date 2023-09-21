package com.actively.recorder.usecase

import android.content.Context
import com.actively.repository.ActivityRecordingRepository
import com.actively.synchronizer.usecases.LaunchSynchronizationUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

class StopRecordingUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val context = mockk<Context>(relaxed = true)
    val syncUseCase = mockk<LaunchSynchronizationUseCase>(relaxUnitFun = true)
    val repository = mockk<ActivityRecordingRepository>(relaxed = true)
    val stopRecordingUseCase = StopRecordingUseCaseImpl(syncUseCase, repository, context)

    test("Should start foreground service") {
        stopRecordingUseCase("Morning Activity")
        verify(exactly = 1) { context.startForegroundService(any()) }
    }

    test("Should mark activity as recorded") {
        stopRecordingUseCase("Morning Activity")
        coVerify(exactly = 1) { repository.markActivityAsRecorded() }
    }

    test("Should update title of activity") {
        stopRecordingUseCase("Morning Activity")
        coVerify(exactly = 1) { repository.updateRecordingActivityTitle("Morning Activity") }
    }

    test("Should launch synchronization") {
        stopRecordingUseCase("Morning activity")
        verify(exactly = 1) { syncUseCase() }
    }
})
