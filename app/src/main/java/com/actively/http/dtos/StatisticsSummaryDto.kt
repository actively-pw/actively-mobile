package com.actively.http.dtos

import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.statistics.StatPage
import com.actively.statistics.StatsPeriod
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
    val avgWeekly: AvgWeeklyStatsDto,
    val yearToDate: YearToDateStatsDto,
    val allTime: AllTimeStatsDto,
) {

    fun toStatPage() = StatPage(
        sport = sport,
        avgWeekly = avgWeekly.toAvgWeeklyStats(),
        yearToDate = yearToDate.toYearToDateStats(),
        allTime = allTime.toAllTimeStats()
    )
}

@Serializable
data class AvgWeeklyStatsDto(
    val time: Long,
    val distance: Double,
    val activitiesNumber: Int,
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
    val activitiesNumber: Int,
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
    val activitiesNumber: Int,
    val distance: Double,
    val longestDistance: Double,
) {

    fun toAllTimeStats() = StatsPeriod.AllTime(
        activitiesNumber = activitiesNumber,
        distance = distance.kilometers,
        longestDistance = longestDistance.kilometers
    )
}
