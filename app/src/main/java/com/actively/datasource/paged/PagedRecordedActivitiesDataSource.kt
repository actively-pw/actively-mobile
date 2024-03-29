package com.actively.datasource.paged

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.actively.activity.RecordedActivity
import com.actively.repository.RecordedActivitiesRepository

/**
 * Paging3 datasource that returns paged recorded activities.
 */
class PagedRecordedActivitiesDataSource(
    private val recordedActivitiesRepository: RecordedActivitiesRepository
) : PagingSource<Int, RecordedActivity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecordedActivity> = try {
        val nextPageNumber = params.key ?: 1
        val page = recordedActivitiesRepository.getActivitiesPage(
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
