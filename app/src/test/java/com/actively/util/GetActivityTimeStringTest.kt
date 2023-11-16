package com.actively.util

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
        val expected = "Today at 10:53"
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
        val expected = "Yesterday at 10:53"
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
        val expected = "1 January 2023 at 10:53"
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
        val expected = "Yesterday at 10:53"
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
        val expected = "Yesterday at 10:53"
        val actual = getActivityTimeString(
            start = start.toInstant(TimeZone.currentSystemDefault()),
            now = now.toInstant(TimeZone.currentSystemDefault())
        )
        actual shouldBe expected
    }
})
