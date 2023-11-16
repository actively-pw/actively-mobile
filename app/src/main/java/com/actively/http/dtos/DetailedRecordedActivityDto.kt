package com.actively.http.dtos

import com.actively.activity.DetailedRecordedActivity
import com.actively.activity.RecordedActivity
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class DetailedRecordedActivityDto(
    val id: String,
    val title: String,
    val sport: Int,
    val start: Instant,
    val stats: StatsDto,
    val routeUrl: String,
    val mapUrl: String,
) {

    @Serializable
    data class StatsDto(
        val distance: Double, //kilometers
        val duration: Double, //milliseconds
        val averageSpeed: Double, //kmph
        val maxSpeed: Double,  //kmph
        val sumOfAscent: Int, //meters
        val sumOfDescent: Int, //meters
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
        sport = "",
        stats = stats.toStats(),
        routeUrl = routeUrl,
        mapUrl = mapUrl,
    )
}
