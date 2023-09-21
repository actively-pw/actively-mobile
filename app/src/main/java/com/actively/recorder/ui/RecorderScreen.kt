package com.actively.recorder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.map.RecorderMap
import com.actively.recorder.RecorderState
import com.actively.ui.theme.ActivelyTheme
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.recorderScreen(navController: NavController) {
    composable("recording_screen") {
        val viewModel = getViewModel<RecorderViewModel>()
        val route by viewModel.route.collectAsState()
        val controlsState by viewModel.controlsState.collectAsState()
        val stats by viewModel.stats.collectAsState()
        RecorderScreen(
            stats = stats,
            route = route,
            controlsState = controlsState,
            onStartRecordingClick = viewModel::startRecording,
            onPauseRecordingClick = viewModel::pauseRecording,
            onResumeRecordingClick = viewModel::resumeRecording,
            onStopRecordingClick = {
                navController.navigate("save_screen")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecorderScreen(
    stats: StatisticsState,
    route: String?,
    controlsState: ControlsState,
    onStartRecordingClick: () -> Unit,
    onPauseRecordingClick: () -> Unit,
    onResumeRecordingClick: () -> Unit,
    onStopRecordingClick: () -> Unit,
) {
    ActivelyTheme {
        Scaffold {
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
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedRecorderControlsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End),
                    controlsState = controlsState,
                    onStartClick = onStartRecordingClick,
                    onPauseClick = onPauseRecordingClick,
                    onResumeClick = onResumeRecordingClick,
                    onStopClick = onStopRecordingClick,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatsSection(stats: StatisticsState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(4.dp))
        LabeledValue(
            label = "Time (s)",
            value = stats.totalTime
        )
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LabeledValue(
                modifier = Modifier.weight(1f),
                label = "Distance (km)",
                value = stats.distance
            )
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            LabeledValue(
                modifier = Modifier.weight(1f),
                label = "Avg speed (km/h)",
                value = stats.averageSpeed
            )
        }
        Divider(modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun LabeledValue(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), style = MaterialTheme.typography.titleSmall)
        Text(text = value.uppercase(), style = MaterialTheme.typography.headlineLarge)
    }
}


@Preview(showBackground = true)
@Composable
private fun BottomSectionPreview() {
    ActivelyTheme {
        Column {
            StatsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                stats = StatisticsState()
            )
            Spacer(Modifier.height(16.dp))
            AnimatedRecorderControlsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                controlsState = ControlsState(RecorderState.Idle, RecorderState.Idle),
                onStartClick = { },
                onPauseClick = { },
                onResumeClick = { },
                onStopClick = { },
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatPreview() {
    ActivelyTheme {
        LabeledValue(label = "Time", value = "01:58:34")
    }
}
