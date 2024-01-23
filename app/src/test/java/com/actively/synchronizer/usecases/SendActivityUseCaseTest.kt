package com.actively.synchronizer.usecases

import com.actively.repository.ActivityRecordingRepository
import com.actively.stubs.stubActivity
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SendActivityUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    coroutineTestScope = true
    val repository = mockk<ActivityRecordingRepository>()

    test("Calls repository to sync activity") {
        coEvery { repository.syncActivity(stubActivity()) } returns Unit
        val useCase = SendActivityUseCaseImpl(repository)
        useCase(stubActivity())
        coVerify(exactly = 1) { repository.syncActivity(stubActivity()) }
    }

    test("Return success if no exception was thrown from repository") {
        coEvery { repository.syncActivity(stubActivity()) } returns Unit
        val useCase = SendActivityUseCaseImpl(repository)
        useCase(stubActivity()) shouldBe Result.success(Unit)
    }

    test("Return failure if exception was thrown from repository") {
        val exception = IllegalArgumentException()
        coEvery { repository.syncActivity(stubActivity()) } throws exception
        val useCase = SendActivityUseCaseImpl(repository)
        useCase(stubActivity()) shouldBe Result.failure(exception)
    }
})
