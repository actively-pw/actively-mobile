package com.actively.datasource

import com.actively.activity.RecordedActivity
import com.actively.http.dtos.RecordedActivityDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

data class RecordedActivitiesPage(
    val data: List<RecordedActivity>,
    val nextPage: Int?
)

interface RecordedActivitiesDataSource {

    suspend fun get(pageNumber: Int, pageSize: Int): RecordedActivitiesPage
}

class RecordedActivitiesDataSourceImpl(
    private val client: HttpClient
) : RecordedActivitiesDataSource {

    override suspend fun get(pageNumber: Int, pageSize: Int): RecordedActivitiesPage {
        val response = client.get("https://activelypw.azurewebsites.net/Activities") {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("Page", "$pageNumber")
                parameters.append("ItemsPerPage", pageSize.toString())
            }
        }
        return RecordedActivitiesPage(
            data = response.body<List<RecordedActivityDto>>()
                .map(RecordedActivityDto::toRecordedActivity),
            nextPage = response.headers[NEXT_PAGE_HEADER]?.toInt()?.takeIf { it >= 1 }
        )
    }

    private companion object {
        const val NEXT_PAGE_HEADER = "nextpage"
    }
}
