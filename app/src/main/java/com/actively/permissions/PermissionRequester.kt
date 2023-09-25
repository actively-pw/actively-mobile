package com.actively.permissions

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.requestPermissionsIfNotGranted(
    onShowRationale: () -> Unit,
    onPermissionsGranted: () -> Unit,
) {
    when {
        allPermissionsGranted -> onPermissionsGranted()
        shouldShowRationale -> onShowRationale()
        else -> launchMultiplePermissionRequest()
    }
}
