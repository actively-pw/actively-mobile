package com.actively.home

import app.cash.turbine.test
import com.actively.datasource.RecordedActivitiesDataSourceFactory
import com.actively.datasource.paged.PagedRecordedActivitiesDataSource
import com.actively.home.ui.HomeViewModel
import com.actively.synchronizer.WorkState
import com.actively.synchronizer.usecases.GetSyncStateUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    coroutineTestScope = true
    val recordedActivitiesDataSourceFactory = mockk<RecordedActivitiesDataSourceFactory>()
    val pagedRecordedActivitiesDataSource = mockk<PagedRecordedActivitiesDataSource>(relaxed = true)
    every { recordedActivitiesDataSourceFactory.create() } returns pagedRecordedActivitiesDataSource
    val getSyncStateUseCase = mockk<GetSyncStateUseCase>()

    test("syncState should represent current state returned from getSyncStateUseCase") {
        every { getSyncStateUseCase() } returns flowOf(WorkState.Enqueued, WorkState.Running)
        val viewModel = HomeViewModel(getSyncStateUseCase, recordedActivitiesDataSourceFactory)
        viewModel.syncState.test {
            awaitItem() shouldBe WorkState.Running
        }
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }
})
