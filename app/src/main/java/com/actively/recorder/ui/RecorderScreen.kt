package com.actively.recorder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.actively.activity.Activity
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.kilometers
import com.actively.map.RecorderMap
import com.actively.recorder.RecorderState
import com.actively.ui.theme.ActivelyTheme
import org.koin.androidx.compose.getViewModel
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecorderScreen(viewModel: RecorderViewModel = getViewModel()) {
    ActivelyTheme {
        Scaffold {
            val stats by viewModel.stats.collectAsState()
            val route by viewModel.route.collectAsState()
            val recordingState by viewModel.recordingState.collectAsState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                RecorderMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), routeGeoJson = route
                )
                StatsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End),
                    stats = stats
                )
                RecorderControlsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End),
                    recordingState = recordingState,
                    onStartClick = viewModel::startRecording,
                    onPauseClick = viewModel::pauseRecording,
                    onResumeClick = viewModel::resumeRecording,
                    onStopClick = viewModel::stopRecording,
                )
            }
        }
    }
}

@Composable
private fun StatsSection(stats: Activity.Stats?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(4.dp))
        Stat(
            label = "Time (s)",
            value = String.format(
                "%02d:%02d:%02d",
                stats?.totalTime?.inWholeHours ?: 0,
                stats?.totalTime?.inWholeMinutes?.mod(60) ?: 0,
                stats?.totalTime?.inWholeSeconds?.mod(60) ?: 0,
            )
        )
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Stat(
                modifier = Modifier.weight(1f),
                label = "Distance (km)",
                value = String.format("%.2f", stats?.distance?.inKilometers ?: 0.0)
            )
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            Stat(
                modifier = Modifier.weight(1f),
                label = "Avg speed (km/h)",
                value = String.format("%.2f", stats?.averageSpeed ?: 0.0)
            )
        }
        Divider(modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun Stat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), style = MaterialTheme.typography.bodySmall)
        Text(value.uppercase(), style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun RecorderControlsSection(
    recordingState: RecorderState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        when (recordingState) {
            is RecorderState.Idle -> Button(
                onClick = onStartClick,
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
            }

            is RecorderState.Started -> Button(
                onClick = onPauseClick,
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(Icons.Filled.Pause, contentDescription = null)
            }

            is RecorderState.Paused -> {
                OutlinedButton(
                    modifier = Modifier.width(80.dp),
                    onClick = onResumeClick,
                    contentPadding = PaddingValues(4.dp),
                ) {
                    Text("Resume", style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    modifier = Modifier.width(80.dp),
                    onClick = onStopClick,
                    contentPadding = PaddingValues(4.dp),
                ) {
                    Text("Finish", style = MaterialTheme.typography.labelMedium)
                }
            }

            is RecorderState.Stopped -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsSectionPreview() {
    ActivelyTheme {
        StatsSection(
            stats = Activity.Stats(1.5.hours, 24.5.kilometers, 23.123123),
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatPreview() {
    ActivelyTheme {
        Stat(label = "Time", value = "01:58:34")
    }
}

@Preview(showBackground = true)
@Composable
fun RecorderControlsSectionPreview() {
    ActivelyTheme {
        RecorderControlsSection(recordingState = RecorderState.Idle, {}, {}, {}, {})
    }
}
