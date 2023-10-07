package com.actively.repository

import com.actively.datasource.AuthTokensDataSource
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class AuthRepositoryTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val authTokensDataSource = mockk<AuthTokensDataSource>(relaxUnitFun = true)
    val client = mockk<HttpClient>()
    val repository = AuthRepositoryImpl(authTokensDataSource, client)

    test("isUserLoggedIn returns true if access and refresh tokens are present") {
        coEvery { authTokensDataSource.getAccessToken() } returns "access-token"
        coEvery { authTokensDataSource.getRefreshToken() } returns "refresh-token"
        repository.isUserLoggedIn().shouldBeTrue()
    }

    test("isUserLoggedIn returns false if tokens are missing") {
        coEvery { authTokensDataSource.getAccessToken() } returns null
        coEvery { authTokensDataSource.getRefreshToken() } returns null
        repository.isUserLoggedIn().shouldBeFalse()
    }

    test("getAccessToken calls AuthTokensDataSource") {
        coEvery { authTokensDataSource.getAccessToken() } returns "access-token"
        repository.getAccessToken() shouldBe "access-token"
        coVerify(exactly = 1) { authTokensDataSource.getAccessToken() }
    }

    test("setAccessToken calls AuthTokensDataSource") {
        repository.setAccessToken("access-token")
        coVerify(exactly = 1) { authTokensDataSource.setAccessToken("access-token") }
    }

    test("getRefreshToken calls AuthTokensDataSource") {
        coEvery { authTokensDataSource.getRefreshToken() } returns "refresh-token"
        repository.getRefreshToken() shouldBe "refresh-token"
        coVerify(exactly = 1) { authTokensDataSource.getRefreshToken() }
    }

    test("setRefreshToken calls AuthTokensDataSource") {
        repository.setRefreshToken("refresh-token")
        coVerify(exactly = 1) { authTokensDataSource.setRefreshToken("refresh-token") }
    }

    test("logout call datasource to clear tokens") {
        repository.logout()
        coVerify(exactly = 1) { authTokensDataSource.clearTokens() }
    }
})
