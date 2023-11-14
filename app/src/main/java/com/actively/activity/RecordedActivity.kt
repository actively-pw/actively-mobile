package com.actively.activity

data class RecordedActivity(
    val id: Id,
    val title: String,
    val sport: String,
    val stats: Activity.Stats,
    val routeUrl: String,
    val mapUrl: String,
) {

    data class Id(val value: String)
}
