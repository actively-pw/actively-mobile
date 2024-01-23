package com.actively.repository

import com.actively.asserts.shouldBe
import com.actively.datasource.AuthTokensDataSource
import com.actively.http.client.KtorClient
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class AuthRepositoryTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val authTokensDataSource = mockk<AuthTokensDataSource>(relaxUnitFun = true)
    val client = mockk<KtorClient>()
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

    test("getBearerTokens calls AuthTokensDataSource") {
        coEvery { authTokensDataSource.getAccessToken() } returns "access-token"
        coEvery { authTokensDataSource.getRefreshToken() } returns "refresh-token"
        repository.getBearerTokens() shouldBe BearerTokens("access-token", "refresh-token")
        coVerify(exactly = 1) { authTokensDataSource.getAccessToken() }
    }

    test("setAccessToken calls AuthTokensDataSource") {
        repository.setBearerTokens(BearerTokens("access-token", "refresh-token"))
        coVerify(exactly = 1) { authTokensDataSource.setAccessToken("access-token") }
        coVerify(exactly = 1) { authTokensDataSource.setRefreshToken("refresh-token") }
    }

    test("logout call datasource to clear tokens") {
        repository.logout()
        coVerify(exactly = 1) { authTokensDataSource.clearTokens() }
    }
})
