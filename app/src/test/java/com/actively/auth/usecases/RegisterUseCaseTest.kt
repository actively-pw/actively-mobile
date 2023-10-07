package com.actively.auth.usecases

import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.auth.Tokens
import com.actively.repository.AuthRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class RegisterUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest

    val authRepository = mockk<AuthRepository>(relaxUnitFun = true)
    val useCase = RegisterUseCaseImpl(authRepository)
    val credentials = Credentials.Register(
        name = "user",
        surname = "surname",
        email = "user@mail.com",
        password = "password"
    )
    coEvery { authRepository.register(any()) } returns Tokens("access", "refresh")

    test("calls repository register with given credentials") {
        useCase(credentials)
        coVerify(exactly = 1) { authRepository.register(credentials) }
    }

    test("sets tokens returned from register") {
        useCase(credentials)
        coVerify(exactly = 1) { authRepository.setAccessToken("access") }
        coVerify(exactly = 1) { authRepository.setRefreshToken("refresh") }
    }

    test("Returns Success if register did not throw any exception") {
        useCase(credentials) shouldBe AuthResult.Success
    }

    test("Returns AccountExists if register threw ClientRequestException") {
        val response = mockk<HttpResponse>(relaxed = true)
        coEvery { authRepository.register(any()) } throws ClientRequestException(
            response,
            "message"
        )
        useCase(credentials) shouldBe AuthResult.AccountExists
    }

    test("Returns Error if register threw other exception than ClientRequestException") {
        val response = mockk<HttpResponse>(relaxed = true)
        coEvery { authRepository.register(any()) } throws ServerResponseException(
            response,
            "message"
        )
        useCase(credentials) shouldBe AuthResult.Error
    }
})
