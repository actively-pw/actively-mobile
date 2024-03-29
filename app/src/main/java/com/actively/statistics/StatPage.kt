package com.actively.statistics

import com.actively.activity.Discipline
import com.actively.distance.Distance
import kotlin.time.Duration

data class StatPage(
    val sport: Discipline,
    val avgWeekly: StatsPeriod.AvgWeekly,
    val yearToDate: StatsPeriod.YearToDate,
    val allTime: StatsPeriod.AllTime,
)

sealed class StatsPeriod {

    data class AvgWeekly(
        val time: Duration,
        val distance: Distance,
        val activitiesNumber: Int,
    ) : StatsPeriod()

    data class YearToDate(
        val time: Duration,
        val distance: Distance,
        val activitiesNumber: Int,
        val elevationGain: Distance,
    ) : StatsPeriod()

    data class AllTime(
        val activitiesNumber: Int,
        val distance: Distance,
        val longestDistance: Distance
    ) : StatsPeriod()
}
