package com.actively.recorder.usecase

import android.content.Context
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify

class PauseRecordingUseCaseTest : FunSpec({

    val context = mockk<Context>(relaxed = true)
    val pauseRecordingUseCase = PauseRecordingUseCaseImpl(context)

    test("Should start foreground service") {
        pauseRecordingUseCase()
        verify(exactly = 1) { context.startForegroundService(any()) }
    }
})
