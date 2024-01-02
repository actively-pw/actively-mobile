package com.actively.http.dtos

import com.actively.activity.toDiscipline
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.statistics.StatPage
import com.actively.statistics.StatsPeriod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class StatisticsSummaryDto(
    val cycling: StatPageDto,
    val running: StatPageDto,
    val nordicWalking: StatPageDto,
) {

    fun toStatPageList() = listOf(cycling, running, nordicWalking).map(StatPageDto::toStatPage)
}

@Serializable
data class StatPageDto(
    val sport: String,
    @SerialName("weekly") val avgWeekly: AvgWeeklyStatsDto,
    val yearToDate: YearToDateStatsDto,
    val allTime: AllTimeStatsDto,
) {

    fun toStatPage() = StatPage(
        sport = sport.toDiscipline(),
        avgWeekly = avgWeekly.toAvgWeeklyStats(),
        yearToDate = yearToDate.toYearToDateStats(),
        allTime = allTime.toAllTimeStats()
    )
}

@Serializable
data class AvgWeeklyStatsDto(
    val time: Long,
    val distance: Double,
    @SerialName("activitiesCount") val activitiesNumber: Int,
) {

    fun toAvgWeeklyStats() = StatsPeriod.AvgWeekly(
        time = time.milliseconds,
        distance = distance.kilometers,
        activitiesNumber = activitiesNumber
    )
}

@Serializable
data class YearToDateStatsDto(
    val time: Long,
    val distance: Double,
    @SerialName("activitiesCount") val activitiesNumber: Int,
    val elevationGain: Int,
) {

    fun toYearToDateStats() = StatsPeriod.YearToDate(
        time = time.milliseconds,
        distance = distance.kilometers,
        activitiesNumber = activitiesNumber,
        elevationGain = elevationGain.meters
    )
}

@Serializable
data class AllTimeStatsDto(
    @SerialName("activitiesCount") val activitiesNumber: Int,
    val distance: Double,
    val longestDistance: Double,
) {

    fun toAllTimeStats() = StatsPeriod.AllTime(
        activitiesNumber = activitiesNumber,
        distance = distance.kilometers,
        longestDistance = longestDistance.kilometers
    )
}
