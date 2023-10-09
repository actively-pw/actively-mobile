package com.actively.auth.usecases

import com.actively.asserts.tokensEq
import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.repository.AuthRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.statement.HttpResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class LogInUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val authRepository = mockk<AuthRepository>(relaxUnitFun = true)
    val useCase = LogInUseCaseImpl(authRepository)
    val credentials = Credentials.Login("user@mail.com", "password")
    coEvery { authRepository.login(any()) } returns BearerTokens("access", "refresh")

    test("calls repository login with given credentials") {
        useCase(credentials)
        coVerify(exactly = 1) { authRepository.login(credentials) }
    }

    test("sets tokens returned from login") {
        useCase(credentials)
        coVerify(exactly = 1) {
            authRepository.setBearerTokens(tokensEq(BearerTokens("access", "refresh")))
        }
    }

    test("Returns Success if login did not throw any exception") {
        useCase(credentials) shouldBe AuthResult.Success
    }

    test("Returns InvalidCredentials if login threw ClientRequestException") {
        val response = mockk<HttpResponse>(relaxed = true)
        coEvery { authRepository.login(any()) } throws ClientRequestException(response, "message")
        useCase(credentials) shouldBe AuthResult.InvalidCredentials
    }

    test("Returns Error if login threw other exception than ClientRequestException") {
        val response = mockk<HttpResponse>(relaxed = true)
        coEvery { authRepository.login(any()) } throws ServerResponseException(response, "message")
        useCase(credentials) shouldBe AuthResult.Error
    }
})
