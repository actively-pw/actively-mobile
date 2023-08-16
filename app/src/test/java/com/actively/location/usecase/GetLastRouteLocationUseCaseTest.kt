package com.actively.location.usecase

import com.actively.activity.Activity
import com.actively.repository.ActivityRepository
import com.actively.stubs.stubLocation
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class GetLastRouteLocationUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val activityRepository = mockk<ActivityRepository>(relaxUnitFun = true)
    val getLastRouteLocationUseCase = GetLastRouteLocationUseCaseImpl(activityRepository)

    beforeTest {
        coEvery { activityRepository.getLatestRouteLocation(id = Activity.Id("1")) } returns stubLocation()
        coEvery { activityRepository.getLatestRouteLocation(id = Activity.Id("non-existing-id")) } returns null
    }

    test("Should return latest location") {
        getLastRouteLocationUseCase(id = Activity.Id("1")) shouldBe stubLocation()
    }

    test("Should return null if no location was found or activity does not exist") {
        getLastRouteLocationUseCase(id = Activity.Id("non-existing-id")) shouldBe null
    }

    test("Should call ActivityRepository to get last location") {
        getLastRouteLocationUseCase(id = Activity.Id("1"))
        coVerify(exactly = 1) {
            activityRepository.getLatestRouteLocation(id = Activity.Id("1"))
        }
    }
})
