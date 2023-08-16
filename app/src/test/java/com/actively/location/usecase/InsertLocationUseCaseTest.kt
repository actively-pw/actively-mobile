package com.actively.location.usecase

import com.actively.activity.Activity
import com.actively.repository.ActivityRepository
import com.actively.stubs.stubLocation
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk

class InsertLocationUseCaseTest : FunSpec({

    val activityRepository = mockk<ActivityRepository>(relaxUnitFun = true)
    val insertLocationUseCase = InsertLocationUseCaseImpl(activityRepository)

    test("Should call ActivityRepository to insert location") {
        insertLocationUseCase(location = stubLocation(), id = Activity.Id("1"))
        coVerify(exactly = 1) {
            activityRepository.insertLocation(
                stubLocation(),
                id = Activity.Id("1")
            )
        }
    }
})
