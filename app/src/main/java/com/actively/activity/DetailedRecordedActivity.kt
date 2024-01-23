package com.actively.activity

import com.actively.distance.Distance
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Holds data for recorder and processed activity details.
 */
data class DetailedRecordedActivity(
    val id: RecordedActivity.Id,
    val title: String,
    val sport: Discipline,
    val start: Instant,
    val stats: Stats,
    val routeUrl: String,
    val lightMapUrl: String,
    val darkMapUrl: String,
) {

    data class Stats(
        val distance: Distance,
        val totalTime: Duration,
        val averageSpeed: Double,
        val maxSpeed: Double,
        val sumOfAscent: Distance,
        val sumOfDescent: Distance
    )
}
