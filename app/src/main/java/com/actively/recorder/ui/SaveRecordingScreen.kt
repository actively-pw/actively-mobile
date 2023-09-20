package com.actively.recorder.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.actively.R
import com.actively.ui.theme.ActivelyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveRecordingScreen() {
    ActivelyTheme {
        Scaffold(
            topBar = {
                TopBar(onBackClick = { }, onSaveClick = {})
            }
        ) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.save_activity_top_bar_title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            TextButton(onClick = onSaveClick) {
                Text(stringResource(id = R.string.save_button).uppercase())
            }
        }
    )
}

@Preview
@Composable
fun SaveRecordingScreenPreview() {
    SaveRecordingScreen()
}
