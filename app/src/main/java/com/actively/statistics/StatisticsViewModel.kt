package com.actively.statistics

import androidx.lifecycle.ViewModel
import com.actively.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatisticsViewModel : ViewModel() {

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    fun onSelectTab(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    private fun initState() = StatisticsState(
        tabs = listOf(
            StatTab(
                sport = R.string.cycling,
                avgWeekly = listOf(
                    LabeledValue(R.string.rides, "0"),
                    LabeledValue(R.string.time, "0h"),
                    LabeledValue(R.string.distance, "0 km"),
                ),
                yearToDate = listOf(
                    LabeledValue(R.string.rides, "0"),
                    LabeledValue(R.string.time, "0h"),
                    LabeledValue(R.string.distance, "0 km"),
                    LabeledValue(R.string.elevation_gain, "0 m"),
                ),
                allTime = listOf(
                    LabeledValue(R.string.rides, "0"),
                    LabeledValue(R.string.distance, "0 km"),
                    LabeledValue(R.string.longest_ride, "0 km"),
                )
            ),
            StatTab(
                sport = R.string.running,
                avgWeekly = listOf(
                    LabeledValue(R.string.runs, "0"),
                    LabeledValue(R.string.time, "0h"),
                    LabeledValue(R.string.distance, "0 km"),
                ),
                yearToDate = listOf(
                    LabeledValue(R.string.runs, "0"),
                    LabeledValue(R.string.time, "0h"),
                    LabeledValue(R.string.distance, "0 km"),
                    LabeledValue(R.string.elevation_gain, "0 m"),
                ),
                allTime = listOf(
                    LabeledValue(R.string.runs, "0"),
                    LabeledValue(R.string.distance, "0 km"),
                    LabeledValue(R.string.longest_run, "0 km"),
                )
            ),
            StatTab(
                sport = R.string.nordic_waling,
                avgWeekly = listOf(
                    LabeledValue(R.string.walks, "0"),
                    LabeledValue(R.string.time, "0h"),
                    LabeledValue(R.string.distance, "0 km"),
                ),
                yearToDate = listOf(
                    LabeledValue(R.string.walks, "0"),
                    LabeledValue(R.string.time, "0h"),
                    LabeledValue(R.string.distance, "0 km"),
                    LabeledValue(R.string.elevation_gain, "0 m"),
                ),
                allTime = listOf(
                    LabeledValue(R.string.walks, "0"),
                    LabeledValue(R.string.distance, "0 km"),
                    LabeledValue(R.string.longest_walk, "0 km"),
                )
            )
        ),
        isLoading = true
    )
}
