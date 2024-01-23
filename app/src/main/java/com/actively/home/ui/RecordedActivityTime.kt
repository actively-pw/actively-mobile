package com.actively.home.ui

/**
 * Represents time in format: <prefix> <time>. Possible prefixes are: Yesterday, Today or specific date.
 * Time is for example 12:54.
 */
data class RecordedActivityTime(
    val time: String,
    val prefix: TimePrefix,
)

sealed class TimePrefix {

    object Yesterday : TimePrefix()
    object Today : TimePrefix()
    data class Date(val value: String) : TimePrefix()
}
