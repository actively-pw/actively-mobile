package com.actively.http.dtos

import com.actively.activity.RecordedActivity
import com.actively.activity.toDiscipline
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RecordedActivityDto(
    val id: String,
    val title: String,
    val sport: String,
    val start: Instant,
    val stats: StatsDto,
    val routeUrl: String,
    val staticMapUrl: String,
) {

    fun toRecordedActivity() = RecordedActivity(
        id = RecordedActivity.Id(id),
        title = title,
        sport = sport.toDiscipline(),
        start = start,
        stats = stats.toActivityStats(),
        routeUrl = routeUrl,
        mapUrl = staticMapUrl,
    )
}


