package com.actively.http.dtos

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsSummaryDto(
    val cycling: StatPageDto,
    val running: StatPageDto,
    val nordicWalking: StatPageDto,
)

@Serializable
data class StatPageDto(
    val sport: String,
    val avgWeekly: WeeklyStatsDto,
    val yearToDate: YearToDateStatsDto,
    val allTime: AllTimeStatsDto,
)

@Serializable
data class WeeklyStatsDto(
    val time: Long,
    val distance: Double,
    val activitiesNumber: Int,
)

@Serializable
data class YearToDateStatsDto(
    val time: Long,
    val distance: Double,
    val activitiesNumber: Int,
    val elevationGain: Int,
)

@Serializable
data class AllTimeStatsDto(
    val activitiesNumber: Int,
    val distance: Double,
    val longestDistance: Double,
)
