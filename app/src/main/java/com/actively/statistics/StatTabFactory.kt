package com.actively.statistics

import com.actively.R
import com.actively.distance.Distance
import com.actively.distance.Distance.Companion.inWholeKilometers
import com.actively.distance.Distance.Companion.inWholeMeters
import kotlin.time.Duration

class StatTabFactory {

    fun create(page: StatPage) = when (page.sport) {
        "cycling" -> cyclingTab(page)
        "running" -> runningTab(page)
        "nordic_walking" -> nordicWalingTab(page)
        else -> cyclingTab(page)
    }

    private fun cyclingTab(page: StatPage) = StatTab(
        sport = R.string.cycling,
        avgWeekly = with(page.avgWeekly) {
            listOf(
                LabeledValue(R.string.rides, activitiesNumber.toString()),
                LabeledValue(R.string.time, formatDuration(time)),
                LabeledValue(R.string.distance, formatDistance(distance)),
            )
        },
        yearToDate = with(page.yearToDate) {
            listOf(
                LabeledValue(R.string.rides, activitiesNumber.toString()),
                LabeledValue(R.string.time, formatDuration(time)),
                LabeledValue(R.string.distance, formatDistance(distance)),
                LabeledValue(R.string.elevation_gain, formatElevation(elevationGain)),
            )
        },
        allTime = with(page.allTime) {
            listOf(
                LabeledValue(R.string.rides, activitiesNumber.toString()),
                LabeledValue(R.string.distance, formatDistance(distance)),
                LabeledValue(R.string.longest_ride, formatDistance(longestDistance)),
            )
        }
    )

    private fun runningTab(page: StatPage) = StatTab(
        sport = R.string.running,
        avgWeekly = with(page.avgWeekly) {
            listOf(
                LabeledValue(R.string.runs, activitiesNumber.toString()),
                LabeledValue(R.string.time, formatDuration(time)),
                LabeledValue(R.string.distance, formatDistance(distance)),
            )
        },
        yearToDate = with(page.yearToDate) {
            listOf(
                LabeledValue(R.string.runs, activitiesNumber.toString()),
                LabeledValue(R.string.time, formatDuration(time)),
                LabeledValue(R.string.distance, formatDistance(distance)),
                LabeledValue(R.string.elevation_gain, formatElevation(elevationGain)),
            )
        },
        allTime = with(page.allTime) {
            listOf(
                LabeledValue(R.string.runs, activitiesNumber.toString()),
                LabeledValue(R.string.distance, formatDistance(distance)),
                LabeledValue(R.string.longest_run, formatDistance(longestDistance)),
            )
        }
    )

    private fun nordicWalingTab(page: StatPage) = StatTab(
        sport = R.string.nordic_walking,
        avgWeekly = with(page.avgWeekly) {
            listOf(
                LabeledValue(R.string.walks, activitiesNumber.toString()),
                LabeledValue(R.string.time, formatDuration(time)),
                LabeledValue(R.string.distance, formatDistance(distance)),
            )
        },
        yearToDate = with(page.yearToDate) {
            listOf(
                LabeledValue(R.string.walks, activitiesNumber.toString()),
                LabeledValue(R.string.time, formatDuration(time)),
                LabeledValue(R.string.distance, formatDistance(distance)),
                LabeledValue(R.string.elevation_gain, formatElevation(elevationGain)),
            )
        },
        allTime = with(page.allTime) {
            listOf(
                LabeledValue(R.string.walks, activitiesNumber.toString()),
                LabeledValue(R.string.distance, formatDistance(distance)),
                LabeledValue(R.string.longest_walk, formatDistance(longestDistance)),
            )
        }
    )

    private fun formatDuration(duration: Duration) = String.format(
        "%dh %dm",
        duration.inWholeHours,
        duration.inWholeSeconds % 60
    )

    private fun formatDistance(dist: Distance) = String.format("%d km", dist.inWholeKilometers)

    private fun formatElevation(elev: Distance) = String.format("%d m", elev.inWholeMeters)
}
