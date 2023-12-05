package com.actively.home.ui

data class RecordedActivityTime(
    val time: String,
    val prefix: TimePrefix,
)

sealed class TimePrefix {

    object Yesterday : TimePrefix()
    object Today : TimePrefix()
    data class Date(val value: String) : TimePrefix()
}
