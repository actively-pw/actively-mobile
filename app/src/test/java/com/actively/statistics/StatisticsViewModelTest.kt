package com.actively.statistics

import app.cash.turbine.test
import com.actively.R
import com.actively.statistics.usecase.GetStatisticsUseCase
import com.actively.stubs.stubStatPage
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    coroutineTestScope = true

    val getStatisticsUseCase = mockk<GetStatisticsUseCase>()
    coEvery { getStatisticsUseCase() } coAnswers {
        delay(500.milliseconds)
        Result.success(
            listOf(
                stubStatPage(sport = "cycling"),
                stubStatPage(sport = "running"),
                stubStatPage(sport = "nordic_walking")
            )
        )
    }
    val statTabFactory = StatTabFactory()

    context("state test") {
        test("init state is correct") {
            val viewModel = StatisticsViewModel(getStatisticsUseCase, statTabFactory)
            viewModel.state.test {
                awaitItem() shouldBe StatisticsState(
                    tabs = listOf(
                        StatTab(R.string.cycling),
                        StatTab(R.string.running),
                        StatTab(R.string.nordic_walking)
                    ),
                    selectedTab = 0,
                    isLoading = true,
                    isError = false
                )
            }
        }

        test("loads state on init") {
            val viewModel = StatisticsViewModel(getStatisticsUseCase, statTabFactory)
            viewModel.state.test {
                awaitItem()
                awaitItem() shouldBe StatisticsState(
                    tabs = listOf(cyclingState(), runningState(), nordicWalingState()),
                    selectedTab = 0,
                    isLoading = false,
                    isError = false
                )
            }
        }

        test("sets error flag if getStatisticsUseCase failed") {
            coEvery { getStatisticsUseCase() } coAnswers {
                delay(500.milliseconds)
                Result.failure(Exception())
            }
            val viewModel = StatisticsViewModel(getStatisticsUseCase, statTabFactory)
            viewModel.state.test {
                awaitItem()
                awaitItem() shouldBe StatisticsState(
                    tabs = listOf(
                        StatTab(sport = R.string.cycling),
                        StatTab(sport = R.string.running),
                        StatTab(sport = R.string.nordic_walking),
                    ),
                    selectedTab = 0,
                    isLoading = false,
                    isError = true
                )
            }
        }
    }

    context("actions") {
        val viewModel = StatisticsViewModel(getStatisticsUseCase, statTabFactory)

        test("onSelectTab properly updates selected tab index") {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.onSelectTab(2)
                awaitItem() shouldBe StatisticsState(
                    tabs = listOf(cyclingState(), runningState(), nordicWalingState()),
                    selectedTab = 2,
                    isLoading = false,
                    isError = false
                )
                viewModel.onSelectTab(1)
                awaitItem() shouldBe StatisticsState(
                    tabs = listOf(cyclingState(), runningState(), nordicWalingState()),
                    selectedTab = 1,
                    isLoading = false,
                    isError = false
                )
                viewModel.onSelectTab(0)
                awaitItem() shouldBe StatisticsState(
                    tabs = listOf(cyclingState(), runningState(), nordicWalingState()),
                    selectedTab = 0,
                    isLoading = false,
                    isError = false
                )
                viewModel.onSelectTab(2)
                awaitItem() shouldBe StatisticsState(
                    tabs = listOf(cyclingState(), runningState(), nordicWalingState()),
                    selectedTab = 2,
                    isLoading = false,
                    isError = false
                )
            }
        }
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }
})

private fun runningState() = StatTab(
    sport = R.string.running,
    avgWeekly = listOf(
        LabeledValue(R.string.runs, "1"),
        LabeledValue(R.string.time, "1h 0m"),
        LabeledValue(R.string.distance, "10 km"),
    ),
    yearToDate = listOf(
        LabeledValue(R.string.runs, "10"),
        LabeledValue(R.string.time, "10h 0m"),
        LabeledValue(R.string.distance, "100 km"),
        LabeledValue(R.string.elevation_gain, "1000 m"),
    ),
    allTime = listOf(
        LabeledValue(R.string.runs, "10"),
        LabeledValue(R.string.distance, "100 km"),
        LabeledValue(R.string.longest_run, "10 km"),
    )
)

private fun cyclingState() = StatTab(
    sport = R.string.cycling,
    avgWeekly = listOf(
        LabeledValue(R.string.rides, "1"),
        LabeledValue(R.string.time, "1h 0m"),
        LabeledValue(R.string.distance, "10 km"),
    ),
    yearToDate = listOf(
        LabeledValue(R.string.rides, "10"),
        LabeledValue(R.string.time, "10h 0m"),
        LabeledValue(R.string.distance, "100 km"),
        LabeledValue(R.string.elevation_gain, "1000 m"),
    ),
    allTime = listOf(
        LabeledValue(R.string.rides, "10"),
        LabeledValue(R.string.distance, "100 km"),
        LabeledValue(R.string.longest_ride, "10 km"),
    )
)

private fun nordicWalingState() = StatTab(
    sport = R.string.nordic_walking,
    avgWeekly = listOf(
        LabeledValue(R.string.walks, "1"),
        LabeledValue(R.string.time, "1h 0m"),
        LabeledValue(R.string.distance, "10 km"),
    ),
    yearToDate = listOf(
        LabeledValue(R.string.walks, "10"),
        LabeledValue(R.string.time, "10h 0m"),
        LabeledValue(R.string.distance, "100 km"),
        LabeledValue(R.string.elevation_gain, "1000 m"),
    ),
    allTime = listOf(
        LabeledValue(R.string.walks, "10"),
        LabeledValue(R.string.distance, "100 km"),
        LabeledValue(R.string.longest_walk, "10 km"),
    )
)
