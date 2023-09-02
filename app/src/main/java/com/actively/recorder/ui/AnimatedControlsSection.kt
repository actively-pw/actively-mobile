package com.actively.recorder.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actively.recorder.RecorderState

@Composable
fun AnimatedRecorderControlsSection(
    controlsState: ControlsState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (currentState, previousState) = controlsState
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        when {
            currentState is RecorderState.Idle || currentState is RecorderState.Stopped -> {
                RoundButton(onClick = onStartClick) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null
                    )
                }
            }

            previousState is RecorderState.Idle && currentState is RecorderState.Started -> {
                RoundButton(onClick = onPauseClick) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Filled.Pause,
                        contentDescription = null
                    )
                }
            }

            currentState is RecorderState.Paused -> {
                SlidingFromCenterResumeFinishButtons(
                    onResumeClick = onResumeClick,
                    onFinishClick = onStopClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            previousState is RecorderState.Paused && currentState is RecorderState.Started -> {
                SlidingTowardsCenterResumeFinishButtons(
                    onPauseClick = onPauseClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            currentState is RecorderState.Started -> {
                RoundButton(onClick = onPauseClick) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Filled.Pause,
                        contentDescription = null
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
fun SlidingFromCenterResumeFinishButtons(
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var targetOffset by remember { mutableStateOf(0.dp) }
    val offset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = tween(350),
        label = "slide_out"
    )
    LaunchedEffect(Unit) {
        targetOffset = 50.dp
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        RoundButton(modifier = Modifier.offset(offset), onClick = onFinishClick) {
            Text("Finish".uppercase(), style = MaterialTheme.typography.titleMedium)
        }
        OutlinedRoundButton(
            modifier = Modifier
                .offset(-offset)
                .background(MaterialTheme.colorScheme.background, CircleShape),
            onClick = onResumeClick
        ) {
            Text("Resume".uppercase(), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun SlidingTowardsCenterResumeFinishButtons(
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var targetOffset by remember { mutableStateOf(50.dp) }
    val offset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = tween(350),
        label = "slide_in"
    )
    LaunchedEffect(Unit) {
        targetOffset = 0.dp
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // this button slides under Pause button so it is there to keep animation concise
        RoundButton(modifier = Modifier.offset(offset), onClick = { }) {
            Text("Finish".uppercase(), style = MaterialTheme.typography.titleMedium)
        }
        RoundButton(modifier = Modifier.offset(-offset), onClick = onPauseClick) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Filled.Pause,
                contentDescription = null
            )
        }
    }
}
