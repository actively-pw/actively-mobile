package com.actively.synchronizer.usecases

import com.actively.activity.Activity
import com.actively.repository.ActivityRecordingRepository
import com.actively.stubs.stubActivity
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SynchronizeActivitiesUseCaseTest : FunSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerTest
    val sendActivityUseCase = mockk<SendActivityUseCase>()
    val repository = mockk<ActivityRecordingRepository>(relaxUnitFun = true)

    context("Test context") {
        val syncUseCase = SynchronizeActivitiesUseCaseImpl(sendActivityUseCase, repository)
        coEvery { repository.getRecordedActivitiesId() } returns listOf(
            Activity.Id("1"),
            Activity.Id("2")
        )
        coEvery { repository.getActivity(Activity.Id("1")) } returns stubActivity(id = "1")
        coEvery { repository.getActivity(Activity.Id("2")) } returns stubActivity(id = "2")
        coEvery { sendActivityUseCase(stubActivity(id = "1")) } returns Result.success(Unit)
        coEvery { sendActivityUseCase(stubActivity(id = "2")) } returns Result.success(Unit)

        test("Calls repository to get ids of recorded activities") {
            syncUseCase()
            coVerify(exactly = 1) { repository.getRecordedActivitiesId() }
        }

        test("Calls repository to get activities by id") {
            syncUseCase()
            coVerify(exactly = 1) { repository.getActivity(Activity.Id("1")) }
            coVerify(exactly = 1) { repository.getActivity(Activity.Id("2")) }
        }

        test("Calls sendActivityUseCase to send activity to backend") {
            syncUseCase()
            coVerify(exactly = 1) { sendActivityUseCase(stubActivity(id = "1")) }
            coVerify(exactly = 1) { sendActivityUseCase(stubActivity(id = "2")) }
        }

        test("Calls repository to remove every synced activity") {
            syncUseCase()
            coVerify(exactly = 1) { repository.removeActivity(Activity.Id("1")) }
            coVerify(exactly = 1) { repository.removeActivity(Activity.Id("2")) }
        }

        test("Removes only those activities that were successfully synchronized") {
            coEvery { sendActivityUseCase(stubActivity(id = "2")) } returns Result.failure(
                IllegalArgumentException()
            )
            syncUseCase()
            coVerify(exactly = 1) { repository.removeActivity(id = Activity.Id("1")) }
            coVerify(exactly = 0) { repository.removeActivity(id = Activity.Id("2")) }
        }

        test("SendActivityUseCase is called only if db returned not null activity by id") {
            coEvery { repository.getActivity(id = Activity.Id("2")) } returns null
            syncUseCase()
            coVerify(exactly = 1) { sendActivityUseCase(stubActivity(id = "1")) }
            coVerify(exactly = 0) { sendActivityUseCase(stubActivity(id = "2")) }
        }
    }
})
