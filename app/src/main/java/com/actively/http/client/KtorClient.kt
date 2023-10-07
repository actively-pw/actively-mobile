package com.actively.http.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


interface KtorClient {

    suspend fun request(endpoint: String, builder: HttpRequestBuilder.() -> Unit): HttpResponse

    fun close()

    companion object {
        const val BASE_URL = "https://activelypw.azurewebsites.net"
    }
}

class UnauthorizedKtorClient : KtorClient {

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
    }

    override suspend fun request(
        endpoint: String,
        builder: HttpRequestBuilder.() -> Unit
    ) = client.request(KtorClient.BASE_URL + endpoint, builder)

    override fun close() {
        client.close()
    }
}
