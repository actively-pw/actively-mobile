package com.actively.http.client

import com.actively.http.dtos.TokensDto
import com.actively.http.dtos.toBearerTokens
import com.actively.http.dtos.toDto
import com.actively.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


interface AuthorizedKtorClient {

    suspend fun request(endpoint: String, builder: HttpRequestBuilder.() -> Unit): HttpResponse

    fun close()

    fun clearCachedTokens()
}

class AuthorizedKtorClientImpl(private val authRepository: AuthRepository) : AuthorizedKtorClient {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
        expectSuccess = true
        install(Auth) {
            bearer {
                loadTokens { authRepository.getBearerTokens() }
                refreshTokens {
                    oldTokens?.let { oldBearerTokens ->
                        client.getFreshBearerTokens(oldBearerTokens)
                            ?.also { authRepository.setBearerTokens(it) }
                    }
                }
            }
        }
    }

    override suspend fun request(
        endpoint: String,
        builder: HttpRequestBuilder.() -> Unit
    ) = client.request(KtorClient.BASE_URL + endpoint, builder)

    override fun clearCachedTokens() {
        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .firstOrNull()?.clearToken()
    }

    override fun close() {
        client.close()
    }

    private suspend fun HttpClient.getFreshBearerTokens(oldBearerTokens: BearerTokens) = try {
        post(KtorClient.BASE_URL + "/Users/refreshTokens") {
            contentType(ContentType.Application.Json)
            setBody(oldBearerTokens.toDto())
        }.body<TokensDto>().toBearerTokens()
    } catch (e: Exception) {
        null
    }
}
