package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.repository.ActivityRepository
import com.actively.stubs.stubActivityStats
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetStatsUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val activityRepository = mockk<ActivityRepository>()
    val getStatsUseCase = GetStatsUseCaseImpl(activityRepository)

    beforeTest {
        every { activityRepository.getStats(id = Activity.Id("1")) } returns flowOf(
            stubActivityStats()
        )
    }

    test("Should return flow of activity stats") {
        getStatsUseCase(id = Activity.Id("1")).first() shouldBe stubActivityStats()
    }

    test("Should call ActivityRepository to get activity stats") {
        getStatsUseCase(id = Activity.Id("1"))
        verify(exactly = 1) { activityRepository.getStats(id = Activity.Id("1")) }
    }

})
