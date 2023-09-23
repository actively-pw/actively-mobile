package com.actively.recorder.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.BuildConfig
import com.actively.map.RecorderMap
import com.actively.recorder.RecorderState
import com.actively.ui.theme.ActivelyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
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
                val locationPermissions = rememberMultiplePermissionsState(
                    listOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                var showDialog by remember { mutableStateOf(false) }
                val requestLocationPermissionFromAppSettings =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
                        showDialog = false
                    }

                if (showDialog) {
                    LocationPermissionDialog(
                        onOpenSettings = {
                            requestLocationPermissionFromAppSettings.launch(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                                )
                            )
                        },
                        onDismissDialog = { showDialog = false })
                }
                AnimatedRecorderControlsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End),
                    controlsState = controlsState,
                    onStartClick = {
                        if (locationPermissions.allPermissionsGranted) {
                            onStartRecordingClick()
                        } else if (locationPermissions.shouldShowRationale) {
                            showDialog = true
                        } else {
                            locationPermissions.launchMultiplePermissionRequest()
                        }
                    },
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

@Composable
fun LocationPermissionDialog(
    onOpenSettings: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(text = "Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissDialog) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Permissions") },
        text = { Text(text = "Location access permission is needed in order to record activity.") }
    )
}

@Preview(showBackground = true)
@Composable
fun StatPreview() {
    ActivelyTheme {
        LabeledValue(label = "Time", value = "01:58:34")
    }
}
