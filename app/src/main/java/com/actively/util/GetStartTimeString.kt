package com.actively.util

import com.actively.home.ui.RecordedActivityTime
import com.actively.home.ui.TimePrefix
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

fun getActivityTimeString(start: Instant, now: Instant): RecordedActivityTime {
    val startDate = start.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val daysDifference = (today.date - startDate.date).days
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timePrefix = when (daysDifference) {
        0 -> TimePrefix.Today
        1 -> TimePrefix.Yesterday
        else -> TimePrefix.Date(startDate.toJavaLocalDateTime().format(dateTimeFormatter))
    }
    val minutes = String.format("%02d", startDate.minute)
    return RecordedActivityTime(time = "${startDate.hour}:${minutes}", prefix = timePrefix)
}
