package com.actively.home.ui

import com.actively.activity.Activity
import com.actively.activity.Discipline
import com.actively.activity.RecordedActivity

data class RecordedActivityUi(
    val id: RecordedActivity.Id,
    val title: String,
    val sport: Discipline,
    val time: RecordedActivityTime,
    val stats: Activity.Stats,
    val lightMapUrl: String,
    val darkMapUrl: String,
)
