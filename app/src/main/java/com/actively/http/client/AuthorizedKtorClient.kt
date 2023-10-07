package com.actively.http.client

import com.actively.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
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
                loadTokens {
                    authRepository.getBearerTokens()
                }
                refreshTokens {
                    oldTokens?.let { oldBearerTokens ->
                        try {
                            authRepository.getFreshBearerTokens(oldBearerTokens).also {
                                authRepository.setBearerTokens(it)
                            }
                        } catch (e: Exception) {
                            null
                        }
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
}
