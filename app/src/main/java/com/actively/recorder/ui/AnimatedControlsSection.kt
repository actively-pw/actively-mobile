package com.actively.recorder.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.actively.recorder.RecorderState
import com.actively.ui.theme.ActivelyTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedRecorderControlsSection(
    recordingState: RecorderState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier,
        targetState = recordingState,
        contentAlignment = Alignment.Center,
        transitionSpec = {
            slideIntoContainer(
                animationSpec = tween(150, easing = EaseInOut),
                towards = AnimatedContentScope.SlideDirection.Down
            ).with(
                slideOutOfContainer(
                    animationSpec = tween(150, easing = EaseInOut),
                    towards = AnimatedContentScope.SlideDirection.Down
                )
            )
        },
        label = "resume"
    ) {
        when (it) {
            is RecorderState.Idle, RecorderState.Stopped -> RoundButton(onClick = onStartClick) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null
                )
            }

            is RecorderState.Started -> RoundButton(onClick = onPauseClick) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Filled.Pause,
                    contentDescription = null
                )
            }

            is RecorderState.Paused -> {
                Row(horizontalArrangement = Arrangement.Center) {
                    OutlinedRoundButton(onClick = onResumeClick) {
                        Text("Resume".uppercase(), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    RoundButton(onClick = onStopClick) {
                        Text("Finish".uppercase(), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartRecordingPreview() {
    ActivelyTheme {
        AnimatedRecorderControlsSection(recordingState = RecorderState.Idle, {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun PauseRecordingPreview() {
    ActivelyTheme {
        AnimatedRecorderControlsSection(recordingState = RecorderState.Started, {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun ResumeFinishRecordingPreview() {
    ActivelyTheme {
        AnimatedRecorderControlsSection(recordingState = RecorderState.Paused, {}, {}, {}, {})
    }
}
