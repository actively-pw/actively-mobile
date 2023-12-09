package com.actively.stubs

import com.actively.distance.Distance
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.statistics.StatPage
import com.actively.statistics.StatsPeriod
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

fun stubStatPage(
    sport: String = "cycling",
    avgWeekly: StatsPeriod.AvgWeekly = stubWeeklyStats(),
    yearToDate: StatsPeriod.YearToDate = stubYearlyStats(),
    allTime: StatsPeriod.AllTime = stubAllTimeStats(),
) = StatPage(
    sport = sport,
    avgWeekly = avgWeekly,
    yearToDate = yearToDate,
    allTime = allTime
)

fun stubWeeklyStats(
    time: Duration = 1.hours,
    distance: Distance = 10.kilometers,
    activitiesNumber: Int = 1,
) = StatsPeriod.AvgWeekly(time, distance, activitiesNumber)

fun stubYearlyStats(
    time: Duration = 10.hours,
    distance: Distance = 100.kilometers,
    activitiesNumber: Int = 10,
    elevationGain: Distance = 1000.meters,
) = StatsPeriod.YearToDate(time, distance, activitiesNumber, elevationGain)

fun stubAllTimeStats(
    activitiesNumber: Int = 10,
    distance: Distance = 100.kilometers,
    longestDistance: Distance = 10.kilometers
) = StatsPeriod.AllTime(activitiesNumber, distance, longestDistance)

