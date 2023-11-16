package com.actively.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

fun getActivityTimeString(start: Instant, now: Instant): String {
    val startDate = start.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val daysDifference = (today.date - startDate.date).days
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timePrefix = when (daysDifference) {
        0 -> "Today"
        1 -> "Yesterday"
        else -> startDate.toJavaLocalDateTime().format(dateTimeFormatter)
    }
    return "$timePrefix at ${startDate.hour}:${startDate.minute}"
}
