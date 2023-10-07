package com.actively.repository

import com.actively.auth.Credentials
import com.actively.auth.Tokens
import com.actively.datasource.AuthTokensDataSource
import com.actively.http.client.AuthorizedKtorClient
import com.actively.http.dtos.TokensDto
import com.actively.http.dtos.toDto
import com.actively.http.dtos.toTokens
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

interface AuthRepository {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun login(credentials: Credentials.Login): Tokens

    suspend fun register(credentials: Credentials.Register): Tokens

    suspend fun refreshAuthTokens(tokens: Tokens): Tokens

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun setAccessToken(accessToken: String)

    suspend fun setRefreshToken(refreshToken: String)

    suspend fun logout()
}

class AuthRepositoryImpl(
    private val authTokensDataSource: AuthTokensDataSource,
    private val client: AuthorizedKtorClient
) : AuthRepository {

    override suspend fun isUserLoggedIn() = authTokensDataSource.getRefreshToken() != null
            && authTokensDataSource.getAccessToken() != null


    override suspend fun login(credentials: Credentials.Login): Tokens {
        val result = client.request("/Users/login") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(credentials.toDto())
        }
        return result.body<TokensDto>().toTokens()
    }

    override suspend fun register(credentials: Credentials.Register): Tokens {
        val result = client.request("/Users/register") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(credentials.toDto())
        }
        return result.body<TokensDto>().toTokens()
    }

    override suspend fun refreshAuthTokens(tokens: Tokens): Tokens {
        val result = client.request("/Users/refreshToken") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(tokens.toDto())
        }
        return result.body<TokensDto>().toTokens()
    }

    override suspend fun getAccessToken() = authTokensDataSource.getAccessToken()

    override suspend fun getRefreshToken() = authTokensDataSource.getRefreshToken()

    override suspend fun setAccessToken(accessToken: String) {
        authTokensDataSource.setAccessToken(accessToken)
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        authTokensDataSource.setRefreshToken(refreshToken)
    }

    override suspend fun logout() {
        authTokensDataSource.clearTokens()
        client.clearCachedTokens()
    }
}


