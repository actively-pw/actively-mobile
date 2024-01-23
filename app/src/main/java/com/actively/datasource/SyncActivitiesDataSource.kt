package com.actively.datasource


import com.actively.activity.Activity
import com.actively.http.client.AuthorizedKtorClient
import com.actively.http.dtos.toDto
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

/**
 * Datasource that sends activity to backend.
 */
interface SyncActivitiesDataSource {

    suspend fun syncActivity(activity: Activity)
}

class SyncActivitiesDataSourceImpl(
    private val client: AuthorizedKtorClient
) : SyncActivitiesDataSource {

    override suspend fun syncActivity(activity: Activity) {
        client.request("/Activities") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(activity.toDto())
        }
    }
}
