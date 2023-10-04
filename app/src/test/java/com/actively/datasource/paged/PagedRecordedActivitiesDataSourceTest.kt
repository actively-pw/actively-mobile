package com.actively.datasource.paged

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.actively.datasource.RecordedActivitiesDataSource
import com.actively.datasource.RecordedActivitiesPage
import com.actively.stubs.stubRecordedActivity
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.coEvery
import io.mockk.mockk

class PagedRecordedActivitiesDataSourceTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val recordedActivitiesDataSource = mockk<RecordedActivitiesDataSource>()
    coEvery { recordedActivitiesDataSource.get(pageSize = 5, pageNumber = 1) }.returns(
        RecordedActivitiesPage(
            data = (1..5).map {
                stubRecordedActivity(id = it.toString())
            },
            nextPage = 2
        )
    )
    coEvery { recordedActivitiesDataSource.get(pageSize = 5, pageNumber = 2) }.returns(
        RecordedActivitiesPage(
            data = (6..10).map {
                stubRecordedActivity(id = it.toString())
            },
            nextPage = 3
        )
    )
    coEvery { recordedActivitiesDataSource.get(pageSize = 5, pageNumber = 3) }.returns(
        RecordedActivitiesPage(
            data = (11..15).map {
                stubRecordedActivity(id = it.toString())
            },
            nextPage = null
        )
    )
    val pagedRecordedActivitiesDataSource =
        PagedRecordedActivitiesDataSource(recordedActivitiesDataSource)

    test("Returns proper page on initial load") {
        val pager = TestPager(
            config = PagingConfig(pageSize = 5),
            pagingSource = pagedRecordedActivitiesDataSource
        )
        val loadResult = pager.refresh(initialKey = 1) as PagingSource.LoadResult.Page
        loadResult.data shouldBe (1..5).map { stubRecordedActivity(id = it.toString()) }
        loadResult.nextKey shouldBe 2
    }

    test("Returns proper pages on consecutive loads") {
        val pager = TestPager(
            config = PagingConfig(pageSize = 5),
            pagingSource = pagedRecordedActivitiesDataSource
        )
        pager.refresh()
        val page2 = pager.append() as PagingSource.LoadResult.Page
        page2.data shouldBe (6..10).map { stubRecordedActivity(id = it.toStr()) }
        page2.nextKey shouldBe 3
        val page3 = pager.append() as PagingSource.LoadResult.Page
        page3.data shouldBe (11..15).map { stubRecordedActivity(id = it.toStr()) }
        page3.nextKey.shouldBeNull()
    }

    test("Returns null if no more pages to load") {
        val pager = TestPager(
            config = PagingConfig(pageSize = 5),
            pagingSource = pagedRecordedActivitiesDataSource
        )
        val page = with(pager) {
            refresh()
            append()
            append()
            append()
        } as? PagingSource.LoadResult.Page
        page.shouldBeNull()
    }
})
