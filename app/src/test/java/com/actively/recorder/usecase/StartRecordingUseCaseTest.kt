package com.actively.recorder.usecase

import android.content.Context
import com.actively.activity.Discipline
import com.actively.activity.usecase.CreateActivityUseCase
import com.actively.repository.ActivityRecordingRepository
import com.actively.stubs.stubActivity
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Instant

class StartRecordingUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val createActivityUseCase = mockk<CreateActivityUseCase>()
    val activityRecordingRepository = mockk<ActivityRecordingRepository>(relaxUnitFun = true)
    val context = mockk<Context>(relaxed = true)
    val startRecordingUseCase = StartRecordingUseCaseImpl(
        createActivityUseCase,
        activityRecordingRepository,
        context
    )

    beforeTest {
        every { createActivityUseCase.invoke(sport = Discipline.Cycling) } returns stubActivity(
            route = emptyList()
        )
    }

    test("Should create activity with provided sport") {
        startRecordingUseCase(Discipline.Cycling, Instant.fromEpochMilliseconds(0))
        verify { createActivityUseCase.invoke(sport = Discipline.Cycling) }
    }

    test("Should insert created activity to repository") {
        startRecordingUseCase(Discipline.Cycling, Instant.fromEpochMilliseconds(0))
        coVerify { activityRecordingRepository.insertRoutelessActivity(stubActivity(route = emptyList())) }
    }

    test("Should insert empty route slice to repository") {
        startRecordingUseCase(Discipline.Cycling, Instant.fromEpochMilliseconds(0))
        coVerify { activityRecordingRepository.insertEmptyRouteSlice(Instant.fromEpochMilliseconds(0)) }
    }

    test("Should start foreground service") {
        startRecordingUseCase(Discipline.Cycling, Instant.fromEpochMilliseconds(0))
        verify(exactly = 1) { context.startForegroundService(any()) }
    }
})
