package com.actively.stubs

import com.actively.R
import com.actively.statistics.LabeledValue
import com.actively.statistics.StatTab

fun stubRunningStatTab() = StatTab(
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

fun stubCyclingStatTab() = StatTab(
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

fun stubNordicWalkingStatTab() = StatTab(
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
