package com.actively.repository

import com.actively.auth.Credentials
import com.actively.datasource.AuthTokensDataSource
import com.actively.http.client.KtorClient
import com.actively.http.dtos.TokensDto
import com.actively.http.dtos.toBearerTokens
import com.actively.http.dtos.toDto
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

/**
 * Repository for data manipulation associated with authentication.
 */
interface AuthRepository {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun login(credentials: Credentials.Login): BearerTokens

    suspend fun register(credentials: Credentials.Register): BearerTokens

    suspend fun getFreshBearerTokens(tokens: BearerTokens): BearerTokens

    suspend fun getBearerTokens(): BearerTokens?

    suspend fun setBearerTokens(bearerTokens: BearerTokens)

    suspend fun logout()
}

class AuthRepositoryImpl(
    private val authBearerTokensDataSource: AuthTokensDataSource,
    private val client: KtorClient
) : AuthRepository {

    override suspend fun isUserLoggedIn() = authBearerTokensDataSource.getRefreshToken() != null
            && authBearerTokensDataSource.getAccessToken() != null


    override suspend fun login(credentials: Credentials.Login): BearerTokens {
        val result = client.request("/Users/login") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(credentials.toDto())
        }
        return result.body<TokensDto>().toBearerTokens()
    }

    override suspend fun register(credentials: Credentials.Register): BearerTokens {
        val result = client.request("/Users/register") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(credentials.toDto())
        }
        return result.body<TokensDto>().toBearerTokens()
    }

    override suspend fun getFreshBearerTokens(tokens: BearerTokens): BearerTokens {
        val result = client.request("/Users/refreshToken") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(tokens.toDto())
        }
        return result.body<TokensDto>().toBearerTokens()
    }

    override suspend fun getBearerTokens(): BearerTokens? {
        val accessToken = authBearerTokensDataSource.getAccessToken() ?: return null
        val refreshToken = authBearerTokensDataSource.getRefreshToken() ?: return null
        return BearerTokens(accessToken, refreshToken)
    }

    override suspend fun setBearerTokens(bearerTokens: BearerTokens) {
        authBearerTokensDataSource.setAccessToken(bearerTokens.accessToken)
        authBearerTokensDataSource.setRefreshToken(bearerTokens.refreshToken)
    }

    override suspend fun logout() {
        authBearerTokensDataSource.clearTokens()
    }
}


