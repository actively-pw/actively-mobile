package com.actively.auth.ui.register

import app.cash.turbine.test
import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.auth.ui.TextFieldState
import com.actively.auth.usecases.RegisterUseCase
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
class RegisterViewModelTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val registerUseCase = mockk<RegisterUseCase>()
    coEvery { registerUseCase(any()) } returns AuthResult.Success

    context("field validation") {
        val viewModel = RegisterViewModel(registerUseCase)

        test("initial state of email field is valid") {
            viewModel.email.value shouldBe TextFieldState(value = "", isValid = true)
        }

        test("onEmailChange updates email field state") {
            viewModel.onEmailChange("mail")
            viewModel.email.value shouldBe TextFieldState(value = "mail", isValid = true)
            viewModel.onEmailChange("mail@mx.co")
            viewModel.email.value shouldBe TextFieldState(value = "mail@mx.co", isValid = true)
            viewModel.onEmailChange("")
            viewModel.email.value shouldBe TextFieldState(value = "", isValid = true)
        }

        test("sonSuccessfulRegister ets email field to not valid if email field is empty") {
            viewModel.onSuccessfulRegister { }
            viewModel.email.value shouldBe TextFieldState(value = "", isValid = false)
        }

        test("onSuccessfulRegister sets email field to not valid if email field contains invalid email") {
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
                viewModel.onSuccessfulRegister { }
                viewModel.email.value shouldBe TextFieldState(value = mailString, isValid = false)
            }
        }

        test("onSuccessfulRegister sets email field to valid if email is valid") {
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onSuccessfulRegister { }
            viewModel.email.value shouldBe TextFieldState(value = "mail@mail.com", isValid = true)
        }

        test("onEmailChange validates email field if email field was marked as invalid") {
            viewModel.onSuccessfulRegister { }
            viewModel.email.value shouldBe TextFieldState("", isValid = false)
            viewModel.onEmailChange("mail")
            viewModel.email.value shouldBe TextFieldState("mail", isValid = false)
            viewModel.onEmailChange("mail@mail")
            viewModel.email.value shouldBe TextFieldState("mail@mail", isValid = false)
            viewModel.onEmailChange("mail@mail.com")
            viewModel.email.value shouldBe TextFieldState("mail@mail.com", isValid = true)
        }

        test("initial state of password field is valid") {
            viewModel.password.value shouldBe TextFieldState(value = "", isValid = true)
        }

        test("onPasswordChange updates password field state") {
            viewModel.onPasswordChange("pass")
            viewModel.password.value shouldBe TextFieldState(value = "pass", isValid = true)
            viewModel.onPasswordChange("password")
            viewModel.password.value shouldBe TextFieldState(value = "password", isValid = true)
            viewModel.onPasswordChange("")
            viewModel.password.value shouldBe TextFieldState(value = "", isValid = true)
        }

        test("onSuccessfulRegister sets password field to not valid if password field is empty") {
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe TextFieldState(value = "", isValid = false)
        }

        test("onSuccessfulRegister sets password field to not valid if password field is shorter than 8 characters") {
            viewModel.onPasswordChange("fffffff")
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe TextFieldState("fffffff", isValid = false)
        }

        test("onSuccessfulRegister sets password field to not valid if password field is longer than 50 characters") {
            val tooLongString = "f".repeat(51)
            viewModel.onPasswordChange(tooLongString)
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe TextFieldState(tooLongString, isValid = false)
        }

        test("onSuccessfulRegister sets password field to valid if password is 8 to 50 characters long") {
            viewModel.onPasswordChange("ffffffff")
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe TextFieldState("ffffffff", isValid = true)
            viewModel.onPasswordChange("f".repeat(50))
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe TextFieldState("f".repeat(50), isValid = true)
        }

        test("onPasswordChange validates password if password was marked as invalid") {
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe TextFieldState("", isValid = false)
            viewModel.onPasswordChange("fff")
            viewModel.password.value shouldBe TextFieldState("fff", isValid = false)
            viewModel.onPasswordChange("f".repeat(51))
            viewModel.password.value shouldBe TextFieldState("f".repeat(51), isValid = false)
            viewModel.onPasswordChange("ffffffffff")
            viewModel.password.value shouldBe TextFieldState("ffffffffff", isValid = true)
        }
    }

    context("register") {
        val viewModel = RegisterViewModel(registerUseCase)

        test("onSuccessfulRegister calls block if register was successful") {
            coEvery { registerUseCase(any()) } returns AuthResult.Success
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            var called = false
            viewModel.onSuccessfulRegister {
                called = true
            }
            called.shouldBeTrue()
        }

        test("onSuccessfulRegister shows registerFailedDialog if account already exists") {
            coEvery { registerUseCase(any()) } returns AuthResult.AccountExists
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            viewModel.showRegistrationFailedDialog.test {
                var called = false
                viewModel.onSuccessfulRegister { called = true }
                called.shouldBeFalse()
                awaitItem().shouldBeTrue()
            }
        }

        test("onSuccessfulRegister calls RegisterUseCase") {
            coEvery { registerUseCase(any()) } returns AuthResult.Success
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            viewModel.onSuccessfulRegister {}
            coVerify(exactly = 1) {
                registerUseCase(
                    Credentials.Register(
                        name = "user",
                        surname = "surname",
                        email = "mail@mail.com",
                        password = "password"
                    )
                )
            }
        }
    }

    test("onDismissRegistrationFailedDialog closes dialog") {
        val viewModel = RegisterViewModel(registerUseCase)
        viewModel.showRegistrationFailedDialog.test {
            viewModel.onDismissRegistrationFailedDialog()
            awaitItem().shouldBeFalse()
        }
    }

    test("changePasswordVisibility properly changes state") {
        val viewModel = RegisterViewModel(registerUseCase)
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
