package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.repository.ActivityRepository
import com.actively.stubs.stubActivityStats
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk

class InsertStatsUseCaseTest : FunSpec({

    val activityRepository = mockk<ActivityRepository>(relaxUnitFun = true)
    val insertStatsUseCase = InsertStatsUseCaseImpl(activityRepository)

    test("Should call ActivityRepository to insert stats") {
        insertStatsUseCase(stats = stubActivityStats(), id = Activity.Id("1"))
        coVerify(exactly = 1) {
            activityRepository.insertStats(
                stubActivityStats(),
                id = Activity.Id("1")
            )
        }
    }
})
