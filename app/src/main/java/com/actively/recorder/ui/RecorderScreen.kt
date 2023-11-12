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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.BuildConfig
import com.actively.R
import com.actively.map.RecorderMap
import com.actively.permissions.requestPermissionsIfNotGranted
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
        val showPermissionRequestDialog by viewModel.showPermissionRequestDialog.collectAsState(
            initial = false
        )
        RecorderScreen(
            stats = stats,
            route = route,
            controlsState = controlsState,
            showPermissionRequestDialog = showPermissionRequestDialog,
            onStartRecordingClick = viewModel::startRecording,
            onPauseRecordingClick = viewModel::pauseRecording,
            onResumeRecordingClick = viewModel::resumeRecording,
            onStopRecordingClick = { navController.navigate("save_screen") },
            onShowPermissionRequestDialog = viewModel::showRequestPermissionDialog,
            onDismissPermissionDialog = viewModel::dismissRequestPermissionDialog,
            onBackClick = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RecorderScreen(
    stats: StatisticsState,
    route: String?,
    controlsState: ControlsState,
    showPermissionRequestDialog: Boolean,
    onStartRecordingClick: () -> Unit,
    onPauseRecordingClick: () -> Unit,
    onResumeRecordingClick: () -> Unit,
    onStopRecordingClick: () -> Unit,
    onShowPermissionRequestDialog: () -> Unit,
    onDismissPermissionDialog: () -> Unit,
    onBackClick: () -> Unit,
) {
    ActivelyTheme {
        Scaffold(topBar = { AppBar(onBackClick = onBackClick) }) {
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
                if (showPermissionRequestDialog) {
                    LocationPermissionRationaleDialog(onDismissDialog = onDismissPermissionDialog)
                }
                AnimatedRecorderControlsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End),
                    controlsState = controlsState,
                    onStartClick = {
                        locationPermissions.requestPermissionsIfNotGranted(
                            onShowRationale = onShowPermissionRequestDialog,
                            onPermissionsGranted = onStartRecordingClick
                        )
                    },
                    onPauseClick = onPauseRecordingClick,
                    onResumeClick = {
                        locationPermissions.requestPermissionsIfNotGranted(
                            onShowRationale = onShowPermissionRequestDialog,
                            onPermissionsGranted = onResumeRecordingClick
                        )
                    },
                    onStopClick = onStopRecordingClick,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.ride)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun StatsSection(stats: StatisticsState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(4.dp))
        LabeledValue(
            label = stringResource(id = R.string.time_in_seconds),
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
                label = stringResource(R.string.distance_km),
                value = stats.distance
            )
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            LabeledValue(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.avg_speed_km_h),
                value = stats.averageSpeed
            )
        }
        Divider(modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun LabeledValue(label: String, value: String, modifier: Modifier = Modifier) {
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
fun LocationPermissionRationaleDialog(onDismissDialog: () -> Unit) {
    val requestLocationPermissionFromAppSettings = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onDismissDialog() }
    )
    val openActivelySettingsIntent = remember {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        )
    }
    AlertDialog(
        onDismissRequest = onDismissDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    requestLocationPermissionFromAppSettings.launch(openActivelySettingsIntent)
                }
            ) {
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

@Preview(showBackground = true)
@Composable
fun LocationPermissionRationaleDialogPreview() {
    ActivelyTheme {
        LocationPermissionRationaleDialog(onDismissDialog = {})
    }
}
