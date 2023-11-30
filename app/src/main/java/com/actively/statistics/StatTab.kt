package com.actively.statistics

import androidx.annotation.StringRes

data class StatTab(
    @StringRes val sport: Int,
    val avgWeekly: List<LabeledValue>,
    val yearToDate: List<LabeledValue>,
    val allTime: List<LabeledValue>
)

data class LabeledValue(
    @StringRes val label: Int,
    val value: String
)



