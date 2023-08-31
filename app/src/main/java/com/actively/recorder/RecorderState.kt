package com.actively.recorder

sealed class RecorderState {
    object Idle : RecorderState()
    object Started : RecorderState()
    object Paused : RecorderState()
    object Stopped : RecorderState()
}

fun RecorderState.asString() = when (this) {
    is RecorderState.Idle -> "idle"
    is RecorderState.Started -> "started"
    is RecorderState.Paused -> "paused"
    is RecorderState.Stopped -> "stopped"
}

fun String.toRecorderState() = when {
    this == "idle" -> RecorderState.Idle
    this == "started" -> RecorderState.Started
    this == "paused" -> RecorderState.Paused
    this == "stopped" -> RecorderState.Stopped
    else -> error("Cannot parse $this to RecorderState")
}
