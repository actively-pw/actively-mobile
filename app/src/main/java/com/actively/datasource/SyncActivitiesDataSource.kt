package com.actively.datasource


import com.actively.activity.Activity
import com.actively.http.dtos.toDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface SyncActivitiesDataSource {

    suspend fun syncActivity(activity: Activity)
}

class SyncActivitiesDataSourceImpl(private val client: HttpClient) : SyncActivitiesDataSource {

    override suspend fun syncActivity(activity: Activity) {
        client.post("https://activelypw.azurewebsites.net/Activities") {
            contentType(ContentType.Application.Json)
            setBody(activity.toDto())
        }
    }
}
