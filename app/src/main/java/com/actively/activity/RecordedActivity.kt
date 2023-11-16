package com.actively.activity

import kotlinx.datetime.Instant

data class RecordedActivity(
    val id: Id,
    val title: String,
    val sport: String,
    val start: Instant,
    val stats: Activity.Stats,
    val routeUrl: String,
    val mapUrl: String,
) {

    data class Id(val value: String)
}
