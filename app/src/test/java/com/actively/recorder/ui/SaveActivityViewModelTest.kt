package com.actively.recorder.ui

import app.cash.turbine.test
import com.actively.recorder.usecase.DiscardActivityUseCase
import com.actively.recorder.usecase.SaveActivityUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SaveActivityViewModelTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val saveActivityUseCase = mockk<SaveActivityUseCase>(relaxUnitFun = true)
    val discardActivityUseCase = mockk<DiscardActivityUseCase>(relaxUnitFun = true)
    val scope = TestScope()
    val viewModel = SaveActivityViewModel(saveActivityUseCase, discardActivityUseCase, scope)

    test("Initial title state is empty string") {
        viewModel.title.value shouldBe ""
    }

    test("onTitleChange updates title state") {
        viewModel.title.test {
            awaitItem() shouldBe ""
            viewModel.onTitleChange("Morning activity")
            awaitItem() shouldBe "Morning activity"
            viewModel.onTitleChange("Evening")
            awaitItem() shouldBe "Evening"
        }
    }

    test("onDiscardClick emits showDiscardDialog true") {
        viewModel.showDiscardDialog.test {
            viewModel.onDiscardClick()
            awaitItem() shouldBe true
        }
    }

    test("onDismissDialog emits showDiscardDialog false") {
        viewModel.showDiscardDialog.test {
            viewModel.onDismissDialog()
            awaitItem() shouldBe false
        }
    }

    test("onConfirmDiscard launches DiscardActivityUseCase") {
        viewModel.onConfirmDiscard()
        coVerify(exactly = 0) { discardActivityUseCase() }
        scope.testScheduler.advanceUntilIdle()
        coVerify(exactly = 1) { discardActivityUseCase() }
    }

    test("onSaveClick launches SaveActivityUseCase with provided title") {
        viewModel.onTitleChange("Activity")
        viewModel.onSaveClick()
        coVerify(exactly = 0) { saveActivityUseCase("Activity") }
        scope.testScheduler.advanceUntilIdle()
        coVerify(exactly = 1) { saveActivityUseCase("Activity") }
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }
})
