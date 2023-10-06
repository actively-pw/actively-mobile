package com.actively.home

import app.cash.turbine.test
import com.actively.auth.usecases.LogOutUseCase
import com.actively.datasource.factory.RecordedActivitiesDataSourceFactory
import com.actively.datasource.paged.PagedRecordedActivitiesDataSource
import com.actively.home.ui.HomeViewModel
import com.actively.synchronizer.WorkState
import com.actively.synchronizer.usecases.GetSyncStateUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
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
    val logoutUseCase = mockk<LogOutUseCase>(relaxUnitFun = true)

    test("syncState should represent current state returned from getSyncStateUseCase") {
        every { getSyncStateUseCase() } returns flowOf(WorkState.Enqueued, WorkState.Running)
        val viewModel = HomeViewModel(
            getSyncStateUseCase,
            recordedActivitiesDataSourceFactory,
            logoutUseCase
        )
        viewModel.syncState.test {
            awaitItem() shouldBe WorkState.Running
        }
    }

    test("onLogout calls LogoutUseCase") {
        every { getSyncStateUseCase() } returns flowOf()
        val viewModel = HomeViewModel(
            getSyncStateUseCase,
            recordedActivitiesDataSourceFactory,
            logoutUseCase
        )
        viewModel.onLogout { }
        coVerify(exactly = 1) { logoutUseCase() }
    }

    test("onLogout calls passed lambda") {
        every { getSyncStateUseCase() } returns flowOf()
        val viewModel = HomeViewModel(
            getSyncStateUseCase,
            recordedActivitiesDataSourceFactory,
            logoutUseCase
        )
        var called = false
        viewModel.onLogout { called = true }
        called.shouldBeTrue()
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }
})
