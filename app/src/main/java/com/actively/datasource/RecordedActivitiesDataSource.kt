package com.actively.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.actively.activity.RecordedActivity
import com.actively.http.dtos.RecordedActivityDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RecordedActivitiesDataSource(
    private val client: HttpClient
) : PagingSource<Int, RecordedActivity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecordedActivity> = try {
        val nextPageNumber = params.key ?: 1
        val response = client.get("https://activelypw.azurewebsites.net/Activities") {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("Page", "$nextPageNumber")
                parameters.append("ItemsPerPage", 5.toString())
            }
        }
        LoadResult.Page(
            data = response.body<List<RecordedActivityDto>>()
                .map(RecordedActivityDto::toRecordedActivity),
            prevKey = null,
            nextKey = response.headers[NEXT_PAGE_HEADER]?.toInt()?.takeIf { it >= 1 }
        ).also(::println)
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, RecordedActivity>): Int? {
        return null
    }

    private companion object {
        const val NEXT_PAGE_HEADER = "nextpage"
    }
}
