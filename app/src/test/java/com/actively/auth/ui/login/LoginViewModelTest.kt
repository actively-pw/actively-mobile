package com.actively.auth.ui.login

import app.cash.turbine.test
import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.auth.usecases.LogInUseCase
import com.actively.field.Field
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val loginUseCase = mockk<LogInUseCase>()
    coEvery { loginUseCase(any()) } returns AuthResult.Success

    context("field validation") {
        val viewModel = LoginViewModel(loginUseCase)

        test("initial state of email field is valid") {
            viewModel.email.value shouldBe Field.State(value = "", isValid = true)
        }

        test("onEmailChange updates email field state") {
            viewModel.onEmailChange("mail")
            viewModel.email.value shouldBe Field.State(value = "mail", isValid = true)
            viewModel.onEmailChange("mail@mx.co")
            viewModel.email.value shouldBe Field.State(value = "mail@mx.co", isValid = true)
            viewModel.onEmailChange("")
            viewModel.email.value shouldBe Field.State(value = "", isValid = true)
        }

        test("onSuccessfulLogin sets email field to not valid if email field is empty") {
            viewModel.onSuccessfulLogin { }
            viewModel.email.value shouldBe Field.State(value = "", isValid = false)
        }

        test("onSuccessfulLogin sets email field to not valid if email field contains invalid email") {
            forAll(
                row("mail"),
                row("mail@"),
                row("mail@co"),
                row("mail@mail."),
                row("mail@mail.c"),
                row("@mail.com"),
                row("mail.com"),
                row(".com"),
            ) { mailString ->
                viewModel.onEmailChange(mailString)
                viewModel.onSuccessfulLogin { }
                viewModel.email.value shouldBe Field.State(value = mailString, isValid = false)
            }
        }

        test("initial state of password field is valid") {
            viewModel.password.value shouldBe Field.State(value = "", isValid = true)
        }

        test("onPasswordChange updates password field state") {
            viewModel.onPasswordChange("pass")
            viewModel.password.value shouldBe Field.State(value = "pass", isValid = true)
            viewModel.onPasswordChange("password")
            viewModel.password.value shouldBe Field.State(value = "password", isValid = true)
            viewModel.onPasswordChange("")
            viewModel.password.value shouldBe Field.State(value = "", isValid = true)
        }

        test("onSuccessfulLogin sets password field to not valid if password field is empty") {
            viewModel.onSuccessfulLogin { }
            viewModel.password.value shouldBe Field.State(value = "", isValid = false)
        }

        test("onSuccessfulLogin sets password field to not valid if password field is shorter than 8 characters") {
            viewModel.onPasswordChange("fffffff")
            viewModel.onSuccessfulLogin { }
            viewModel.password.value shouldBe Field.State("fffffff", isValid = false)
        }

        test("onSuccessfulLogin sets password field to not valid if password field is longer than 50 characters") {
            val tooLongString = "f".repeat(51)
            viewModel.onPasswordChange(tooLongString)
            viewModel.onSuccessfulLogin { }
            viewModel.password.value shouldBe Field.State(tooLongString, isValid = false)
        }
    }

    context("login") {
        val viewModel = LoginViewModel(loginUseCase)

        test("onSuccessfulLogin calls block if login was successful") {
            coEvery { loginUseCase(any()) } returns AuthResult.Success
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            var called = false
            viewModel.onSuccessfulLogin {
                called = true
            }
            called.shouldBeTrue()
        }

        test("onSuccessfulLogin calls onShowLoginFailedDialog if credentials were not valid") {
            coEvery { loginUseCase(any()) } returns AuthResult.InvalidCredentials
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            viewModel.showLoginFailedDialog.test {
                var called = false
                viewModel.onSuccessfulLogin {
                    called = true
                }
                called.shouldBeFalse()
                awaitItem().shouldBeTrue()
            }
        }

        test("Calls LoginUseCase to log in") {
            coEvery { loginUseCase(any()) } returns AuthResult.Success
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            viewModel.onSuccessfulLogin { }
            coVerify(exactly = 1) { loginUseCase(Credentials.Login("mail@mail.com", "password")) }
        }

        test("Initial loginInProgress is false") {
            viewModel.loginInProgress.value.shouldBeFalse()
        }

        test("onSuccessfulLogin updated loginInProgress") {
            coEvery { loginUseCase(any()) } returns AuthResult.Success
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            viewModel.loginInProgress.test {
                awaitItem()
                viewModel.onSuccessfulLogin { }
                awaitItem().shouldBeTrue()
                awaitItem().shouldBeFalse()
            }
        }
    }

    test("onDismissLoginFailedDialog closes dialog") {
        val viewModel = LoginViewModel(loginUseCase)
        viewModel.showLoginFailedDialog.test {
            viewModel.onDismissLoginFailedDialog()
            awaitItem().shouldBeFalse()
        }
    }

    test("changePasswordVisibility properly changes state") {
        val viewModel = LoginViewModel(loginUseCase)
        viewModel.isPasswordVisible.value.shouldBeFalse()
        viewModel.changePasswordVisibility()
        viewModel.isPasswordVisible.value.shouldBeTrue()
        viewModel.changePasswordVisibility()
        viewModel.isPasswordVisible.value.shouldBeFalse()
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }
})
