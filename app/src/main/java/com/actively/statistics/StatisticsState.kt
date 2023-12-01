package com.actively.statistics

import androidx.annotation.StringRes

data class StatisticsState(
    val tabs: List<StatTab> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
)

data class StatTab(
    @StringRes val sport: Int,
    val avgWeekly: List<LabeledValue> = emptyList(),
    val yearToDate: List<LabeledValue> = emptyList(),
    val allTime: List<LabeledValue> = emptyList()
)

data class LabeledValue(
    @StringRes val label: Int,
    val value: String
)
