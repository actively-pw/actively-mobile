package com.actively.activity.usecase

import com.actively.repository.ActivityRepository
import com.actively.stubs.stubActivity
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk

class InsertActivityUseCaseTest : FunSpec({

    val activityRepository = mockk<ActivityRepository>(relaxUnitFun = true)
    val insertActivityUseCase = InsertActivityUseCaseImpl(activityRepository)

    test("Should call ActivityRepository to insert activity") {
        insertActivityUseCase(activity = stubActivity())
        coVerify(exactly = 1) { activityRepository.insertActivity(stubActivity()) }
    }
})
