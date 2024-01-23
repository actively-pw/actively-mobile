package com.actively.activity

import kotlinx.datetime.Instant

/**
 * Model for activity that was already recorder and processed by backend.
 */
data class RecordedActivity(
    val id: Id,
    val title: String,
    val sport: Discipline,
    val start: Instant,
    val stats: Activity.Stats,
    val routeUrl: String,
    val lightMapUrl: String,
    val darkMapUrl: String
) {

    data class Id(val value: String)
}
