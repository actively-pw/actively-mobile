package com.actively.http.dtos

import com.actively.activity.DetailedRecordedActivity
import com.actively.activity.RecordedActivity
import com.actively.activity.toDiscipline
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class DetailedRecordedActivityDto(
    val id: String,
    val title: String,
    val sport: String,
    val start: Instant,
    val stats: StatsDto,
    val routeUrl: String,
    val lightStaticMapUrl: String,
    val darkStaticMapUrl: String,
) {

    @Serializable
    data class StatsDto(
        val distance: Double,
        @SerialName("totalTime") val duration: Double,
        val averageSpeed: Double,
        val maxSpeed: Double,
        val sumOfAscent: Int,
        val sumOfDescent: Int,
    ) {

        fun toStats() = DetailedRecordedActivity.Stats(
            distance = distance.kilometers,
            totalTime = duration.milliseconds,
            averageSpeed = averageSpeed,
            maxSpeed = maxSpeed,
            sumOfAscent = sumOfAscent.meters,
            sumOfDescent = sumOfDescent.meters
        )
    }

    fun toDetailedRecordedActivity() = DetailedRecordedActivity(
        id = RecordedActivity.Id(id),
        title = title,
        sport = sport.toDiscipline(),
        start = start,
        stats = stats.toStats(),
        routeUrl = routeUrl,
        lightMapUrl = lightStaticMapUrl,
        darkMapUrl = darkStaticMapUrl,
    )
}
