package com.actively.repository

import com.actively.datasource.AuthTokensDataSource
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AuthRepositoryTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val authTokensDataSource = mockk<AuthTokensDataSource>(relaxUnitFun = true)
    val client = mockk<HttpClient>()
    val repository = AuthRepositoryImpl(authTokensDataSource, client)

    test("getAccessToken calls AuthTokensDataSource") {
        every { authTokensDataSource.getAccessToken() } returns "access-token"
        repository.getAccessToken() shouldBe "access-token"
        verify(exactly = 1) { authTokensDataSource.getAccessToken() }
    }

    test("setAccessToken calls AuthTokensDataSource") {
        repository.setAccessToken("access-token")
        verify(exactly = 1) { authTokensDataSource.setAccessToken("access-token") }
    }

    test("getRefreshToken calls AuthTokensDataSource") {
        every { authTokensDataSource.getRefreshToken() } returns "refresh-token"
        repository.getRefreshToken() shouldBe "refresh-token"
        verify(exactly = 1) { authTokensDataSource.getRefreshToken() }
    }

    test("setRefreshToken calls AuthTokensDataSource") {
        repository.setRefreshToken("refresh-token")
        verify(exactly = 1) { authTokensDataSource.setRefreshToken("refresh-token") }
    }

    test("logout call datasource to clear tokens") {
        repository.logout()
        verify(exactly = 1) { authTokensDataSource.clearTokens() }
    }
})
