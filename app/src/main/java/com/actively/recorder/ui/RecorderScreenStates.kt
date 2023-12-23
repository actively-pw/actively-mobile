package com.actively.recorder.ui

import com.actively.activity.Activity
import com.actively.activity.Discipline
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.recorder.RecorderState
import kotlin.time.Duration

data class ControlsState(
    val current: RecorderState,
    val previous: RecorderState
)

data class StatisticsState(
    val averageSpeed: String = "0,00",
    val distance: String = "0,00",
    val totalTime: String = "00:00:00",
)

data class DisciplineState(
    val disciplines: List<Discipline> = Discipline.values,
    val selectedDiscipline: Discipline? = null,
    val showSelectSportButton: Boolean = false,
    val showBottomSheet: Boolean = false
)

fun Activity.Stats.toState() = StatisticsState(
    averageSpeed = String.format("%.2f", averageSpeed),
    distance = String.format("%.2f", distance.inKilometers),
    totalTime = totalTime.format()
)

fun Duration.format() = String.format(
    "%02d:%02d:%02d",
    inWholeHours,
    inWholeMinutes.mod(60),
    inWholeSeconds.mod(60),
)
