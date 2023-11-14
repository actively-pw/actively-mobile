package com.actively.datasource.paged

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.actively.activity.RecordedActivity
import com.actively.datasource.RecordedActivitiesDataSource

class PagedRecordedActivitiesDataSource(
    private val recordedActivitiesDataSource: RecordedActivitiesDataSource
) : PagingSource<Int, RecordedActivity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecordedActivity> = try {
        val nextPageNumber = params.key ?: 1
        val page = recordedActivitiesDataSource.get(
            pageNumber = nextPageNumber,
            pageSize = PAGE_SIZE
        )
        LoadResult.Page(
            data = page.data,
            prevKey = null,
            nextKey = page.nextPage
        )
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, RecordedActivity>): Int? {
        return null
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}
