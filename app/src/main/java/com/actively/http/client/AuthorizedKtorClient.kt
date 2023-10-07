package com.actively.http.client

import com.actively.auth.Tokens
import com.actively.repository.AuthRepository
import io.ktor.client.HttpClient
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
import io.ktor.client.request.request
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


interface AuthorizedKtorClient : KtorClient {

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
                    val accessToken = authRepository.getAccessToken() ?: return@loadTokens null
                    val refreshToken = authRepository.getRefreshToken() ?: return@loadTokens null
                    BearerTokens(accessToken, refreshToken).also {
                        println("loaded tokens: Access=$accessToken, Refresh=$refreshToken")
                    }
                }
                refreshTokens {
                    val accessToken = oldTokens?.accessToken ?: return@refreshTokens null
                    val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null
                    val oldTokens = Tokens(accessToken, refreshToken)
                    val refreshedTokens = authRepository.refreshAuthTokens(oldTokens)
                    authRepository.setAccessToken(refreshedTokens.accessToken)
                    authRepository.setRefreshToken(refreshedTokens.refreshToken)
                    BearerTokens(refreshedTokens.accessToken, refreshedTokens.refreshToken).also {
                        println("refreshed tokens: Access=${refreshedTokens.accessToken}, Refresh=${refreshedTokens.refreshToken}")
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
