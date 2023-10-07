package com.actively.auth.ui.register

import app.cash.turbine.test
import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.auth.usecases.RegisterUseCase
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

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class RegisterViewModelTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val registerUseCase = mockk<RegisterUseCase>()
    coEvery { registerUseCase(any()) } returns AuthResult.Success

    context("field validation") {
        val viewModel = RegisterViewModel(registerUseCase)

        test("initial state of name field is valid") {
            viewModel.name.value shouldBe Field.State(value = "", isValid = true)
        }

        test("onNameChange updates name field state") {
            viewModel.onNameChange("user")
            viewModel.name.value shouldBe Field.State(value = "user", isValid = true)
        }

        test("onSuccessfulRegister sets name field to not valid if name field is empty") {
            viewModel.onSuccessfulRegister { }
            viewModel.name.value shouldBe Field.State("", false)
        }

        test("initial state of surname field is valid") {
            viewModel.surname.value shouldBe Field.State(value = "", isValid = true)
        }

        test("onNameChange updates surname field state") {
            viewModel.onSurnameChange("surname")
            viewModel.surname.value shouldBe Field.State(value = "surname", isValid = true)
        }

        test("onSuccessfulRegister sets surname field to not valid if name field is empty") {
            viewModel.onSuccessfulRegister { }
            viewModel.surname.value shouldBe Field.State("", false)
        }

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

        test("sonSuccessfulRegister sets email field to not valid if email field is empty") {
            viewModel.onSuccessfulRegister { }
            viewModel.email.value shouldBe Field.State(value = "", isValid = false)
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

        test("onSuccessfulRegister sets password field to not valid if password field is empty") {
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe Field.State(value = "", isValid = false)
        }

        test("onSuccessfulRegister sets password field to not valid if password field is shorter than 8 characters") {
            viewModel.onPasswordChange("fffffff")
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe Field.State("fffffff", isValid = false)
        }

        test("onSuccessfulRegister sets password field to not valid if password field is longer than 50 characters") {
            val tooLongString = "f".repeat(51)
            viewModel.onPasswordChange(tooLongString)
            viewModel.onSuccessfulRegister { }
            viewModel.password.value shouldBe Field.State(tooLongString, isValid = false)
        }
    }

    context("register") {
        val viewModel = RegisterViewModel(registerUseCase)

        test("onSuccessfulRegister calls block if register was successful") {
            coEvery { registerUseCase(any()) } returns AuthResult.Success
            viewModel.onNameChange("user")
            viewModel.onSurnameChange("surname")
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
            viewModel.onNameChange("user")
            viewModel.onSurnameChange("surname")
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
            viewModel.onNameChange("user")
            viewModel.onSurnameChange("surname")
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

        test("onSuccessfulRegister properly updates registerInProgress state") {
            coEvery { registerUseCase(any()) } returns AuthResult.Success
            viewModel.onNameChange("user")
            viewModel.onSurnameChange("surname")
            viewModel.onEmailChange("mail@mail.com")
            viewModel.onPasswordChange("password")
            viewModel.registerInProgress.test {
                awaitItem().shouldBeFalse()
                viewModel.onSuccessfulRegister { }
                awaitItem().shouldBeTrue()
                awaitItem().shouldBeFalse()
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
