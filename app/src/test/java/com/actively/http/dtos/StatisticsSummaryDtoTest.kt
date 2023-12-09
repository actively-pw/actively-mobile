package com.actively.http.dtos

import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.statistics.StatsPeriod
import com.actively.stubs.stubStatPage
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours

class StatisticsSummaryDtoTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest

    test("Correctly maps AllTimeStatsDto to AllTimeStats") {
        val dto = AllTimeStatsDto(
            activitiesNumber = 10,
            distance = 125.0,
            longestDistance = 40.0
        )
        dto.toAllTimeStats() shouldBe StatsPeriod.AllTime(
            activitiesNumber = 10,
            distance = 125.kilometers,
            longestDistance = 40.kilometers
        )
    }

    test("Correctly maps YearToDateStatsDto to YearToDate") {
        val dto = YearToDateStatsDto(
            activitiesNumber = 10,
            distance = 125.0,
            time = 36_000_000,
            elevationGain = 1000
        )
        dto.toYearToDateStats() shouldBe StatsPeriod.YearToDate(
            time = 10.hours,
            distance = 125.kilometers,
            activitiesNumber = 10,
            elevationGain = 1000.meters
        )
    }

    test("Correctly maps AvgWeeklyStatsDto to AvgWeekly") {
        val dto = AvgWeeklyStatsDto(
            time = 36_000_000,
            activitiesNumber = 10,
            distance = 10.0
        )
        dto.toAvgWeeklyStats() shouldBe StatsPeriod.AvgWeekly(
            time = 10.hours,
            activitiesNumber = 10,
            distance = 10.kilometers
        )
    }

    test("Correctly maps StatPageDto to StatPage") {
        val dto = StatPageDto(
            sport = "cycling",
            avgWeekly = AvgWeeklyStatsDto(
                time = 3_600_000,
                activitiesNumber = 1,
                distance = 10.0
            ),
            yearToDate = YearToDateStatsDto(
                activitiesNumber = 10,
                distance = 100.0,
                time = 36_000_000,
                elevationGain = 1000
            ),
            allTime = AllTimeStatsDto(
                activitiesNumber = 10,
                distance = 100.0,
                longestDistance = 10.0
            )
        )
        dto.toStatPage() shouldBe stubStatPage()
    }

    test("Correctly maps StatisticsSummaryDto to StatPage list") {
        val dto = StatisticsSummaryDto(
            cycling = StatPageDto(
                sport = "cycling",
                avgWeekly = AvgWeeklyStatsDto(
                    time = 3_600_000,
                    activitiesNumber = 1,
                    distance = 10.0
                ),
                yearToDate = YearToDateStatsDto(
                    activitiesNumber = 10,
                    distance = 100.0,
                    time = 36_000_000,
                    elevationGain = 1000
                ),
                allTime = AllTimeStatsDto(
                    activitiesNumber = 10,
                    distance = 100.0,
                    longestDistance = 10.0
                )
            ),
            running = StatPageDto(
                sport = "running",
                avgWeekly = AvgWeeklyStatsDto(
                    time = 3_600_000,
                    activitiesNumber = 1,
                    distance = 10.0
                ),
                yearToDate = YearToDateStatsDto(
                    activitiesNumber = 10,
                    distance = 100.0,
                    time = 36_000_000,
                    elevationGain = 1000
                ),
                allTime = AllTimeStatsDto(
                    activitiesNumber = 10,
                    distance = 100.0,
                    longestDistance = 10.0
                )
            ),
            nordicWalking = StatPageDto(
                sport = "nordic_walking",
                avgWeekly = AvgWeeklyStatsDto(
                    time = 3_600_000,
                    activitiesNumber = 1,
                    distance = 10.0
                ),
                yearToDate = YearToDateStatsDto(
                    activitiesNumber = 10,
                    distance = 100.0,
                    time = 36_000_000,
                    elevationGain = 1000
                ),
                allTime = AllTimeStatsDto(
                    activitiesNumber = 10,
                    distance = 100.0,
                    longestDistance = 10.0
                )
            )
        )
        dto.toStatPageList() shouldBe listOf(
            stubStatPage(sport = "cycling"),
            stubStatPage(sport = "running"),
            stubStatPage(sport = "nordic_walking")
        )
    }
})
