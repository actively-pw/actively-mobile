package com.actively.recorder.usecase

import android.content.Context
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify

class StopRecordingUseCaseTest : FunSpec({

    val context = mockk<Context>(relaxed = true)
    val stopRecordingUseCase = StopRecordingUseCaseImpl(context)

    test("Should start foreground service") {
        stopRecordingUseCase()
        verify(exactly = 1) { context.startService(any()) }
    }
})
