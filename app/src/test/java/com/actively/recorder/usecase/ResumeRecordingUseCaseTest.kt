package com.actively.recorder.usecase

import android.content.Context
import com.actively.repository.ActivityRecordingRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Instant

class ResumeRecordingUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val activityRecordingRepository = mockk<ActivityRecordingRepository>(relaxUnitFun = true)
    val context = mockk<Context>(relaxed = true)
    val resumeRecordingUseCase = ResumeRecordingUseCaseImpl(activityRecordingRepository, context)

    test("Should insert empty route slice to repository") {
        resumeRecordingUseCase(Instant.fromEpochMilliseconds(0))
        coVerify(exactly = 1) {
            activityRecordingRepository.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(0))
        }
    }

    test("Should start foreground service") {
        resumeRecordingUseCase(Instant.fromEpochMilliseconds(0))
        verify(exactly = 1) { context.startForegroundService(any()) }
    }

})
