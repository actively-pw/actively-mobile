package com.actively.recorder.ui

import com.actively.recorder.RecorderState

data class ControlsState(
    val current: RecorderState,
    val previous: RecorderState
)
