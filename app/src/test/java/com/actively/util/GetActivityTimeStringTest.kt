package com.actively.util

import com.actively.home.ui.RecordedActivityTime
import com.actively.home.ui.TimePrefix
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class GetActivityTimeStringTest : FunSpec({

    test("Returns correct string for today's time") {
        val start =
            LocalDateTime(dayOfMonth = 1, monthNumber = 1, year = 2023, hour = 10, minute = 53)
        val now =
            LocalDateTime(dayOfMonth = 1, monthNumber = 1, year = 2023, hour = 12, minute = 30)
        val expected = RecordedActivityTime(time = "10:53", prefix = TimePrefix.Today)
        val actual = getActivityTimeString(
            start = start.toInstant(TimeZone.currentSystemDefault()),
            now = now.toInstant(TimeZone.currentSystemDefault())
        )
        actual shouldBe expected
    }


    test("Return correct string for yesterday's time") {
        val start =
            LocalDateTime(dayOfMonth = 1, monthNumber = 1, year = 2023, hour = 10, minute = 53)
        val now =
            LocalDateTime(dayOfMonth = 2, monthNumber = 1, year = 2023, hour = 12, minute = 30)
        val expected = RecordedActivityTime(time = "10:53", prefix = TimePrefix.Yesterday)
        val actual = getActivityTimeString(
            start = start.toInstant(TimeZone.currentSystemDefault()),
            now = now.toInstant(TimeZone.currentSystemDefault())
        )
        actual shouldBe expected
    }

    test("Return correct string for times that differs more than 1 day") {
        val start =
            LocalDateTime(dayOfMonth = 1, monthNumber = 1, year = 2023, hour = 10, minute = 53)
        val now =
            LocalDateTime(dayOfMonth = 4, monthNumber = 1, year = 2023, hour = 12, minute = 30)
        val expected = RecordedActivityTime(time = "10:53", prefix = TimePrefix.Date("01.01.2023"))
        val actual = getActivityTimeString(
            start = start.toInstant(TimeZone.currentSystemDefault()),
            now = now.toInstant(TimeZone.currentSystemDefault())
        )
        actual shouldBe expected
    }

    test("Return correct string when given times are on the edges of months") {
        val start =
            LocalDateTime(dayOfMonth = 31, monthNumber = 10, year = 2023, hour = 10, minute = 53)
        val now =
            LocalDateTime(dayOfMonth = 1, monthNumber = 11, year = 2023, hour = 12, minute = 30)
        val expected = RecordedActivityTime(time = "10:53", prefix = TimePrefix.Yesterday)
        val actual = getActivityTimeString(
            start = start.toInstant(TimeZone.currentSystemDefault()),
            now = now.toInstant(TimeZone.currentSystemDefault())
        )
        actual shouldBe expected
    }

    test("Return correct string when given times are on the edges of years") {
        val start =
            LocalDateTime(dayOfMonth = 31, monthNumber = 12, year = 2023, hour = 10, minute = 53)
        val now =
            LocalDateTime(dayOfMonth = 1, monthNumber = 1, year = 2024, hour = 12, minute = 30)
        val expected = RecordedActivityTime(time = "10:53", prefix = TimePrefix.Yesterday)
        val actual = getActivityTimeString(
            start = start.toInstant(TimeZone.currentSystemDefault()),
            now = now.toInstant(TimeZone.currentSystemDefault())
        )
        actual shouldBe expected
    }
})
