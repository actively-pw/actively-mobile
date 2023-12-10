package com.actively.repository

import com.actively.activity.DetailedRecordedActivity
import com.actively.activity.RecordedActivity
import com.actively.http.client.AuthorizedKtorClient
import com.actively.http.dtos.DetailedRecordedActivityDto
import com.actively.http.dtos.RecordedActivityDto
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

data class RecordedActivitiesPage(
    val data: List<RecordedActivity>,
    val nextPage: Int?
)

interface RecordedActivitiesRepository {

    suspend fun getActivitiesPage(pageNumber: Int, pageSize: Int): RecordedActivitiesPage

    suspend fun getDetailedActivity(id: RecordedActivity.Id): DetailedRecordedActivity
}

class RecordedActivitiesRepositoryImpl(
    private val client: AuthorizedKtorClient
) : RecordedActivitiesRepository {

    override suspend fun getActivitiesPage(pageNumber: Int, pageSize: Int): RecordedActivitiesPage {
        val response = client.request("/Activities") {
            method = HttpMethod.Get
            contentType(ContentType.Application.Json)
            url {
                parameters.append("Page", "$pageNumber")
                parameters.append("ItemsPerPage", pageSize.toString())
            }
            headers {
                append("staticMapType", "mobileLight")
            }
        }
        return RecordedActivitiesPage(
            data = response.body<List<RecordedActivityDto>>()
                .map(RecordedActivityDto::toRecordedActivity),
            nextPage = response.headers[NEXT_PAGE_HEADER]?.toInt()?.takeIf { it >= 1 }
        )
    }

    override suspend fun getDetailedActivity(id: RecordedActivity.Id): DetailedRecordedActivity {
        val response = client.request("/Activities/${id.value}") {
            method = HttpMethod.Get
            contentType(ContentType.Application.Json)
            headers {
                append("staticMapType", "mobileLight")
            }
        }
        return response.body<DetailedRecordedActivityDto>().toDetailedRecordedActivity()
    }

    private companion object {
        const val NEXT_PAGE_HEADER = "nextpage"
    }
}
