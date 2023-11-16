package com.actively.http.dtos

import com.actively.activity.RecordedActivity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RecordedActivityDto(
    val id: String,
    val title: String,
    val sport: Int,
    val start: Instant,
    val stats: StatsDto,
    val routeUrl: String,
    val staticMapUrl: String,
) {

    fun toRecordedActivity() = RecordedActivity(
        id = RecordedActivity.Id(id),
        title = title,
        sport = "",
        start = start,
        stats = stats.toActivityStats(),
        routeUrl = routeUrl,
        mapUrl = staticMapUrl,
    )
}


