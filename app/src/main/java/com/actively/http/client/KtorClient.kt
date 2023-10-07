package com.actively.http.client

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

interface KtorClient {

    suspend fun request(endpoint: String, builder: HttpRequestBuilder.() -> Unit): HttpResponse

    companion object {
        const val BASE_URL = "https://activelypw.azurewebsites.net"
    }
}
