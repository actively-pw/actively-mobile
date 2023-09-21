package com.actively.recorder.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.actively.R
import com.actively.ui.theme.ActivelyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveRecordingScreen(
    activityTitle: String,
    showDiscardWarningDialog: Boolean,
    onTitleChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onConfirmDiscard: () -> Unit,
    onDismissDialog: () -> Unit,
    onBackClick: () -> Unit
) {
    ActivelyTheme {
        Scaffold(
            topBar = {
                TopBar(onBackClick = onBackClick, onSaveClick = onSaveClick)
            }
        ) {
            if (showDiscardWarningDialog) {
                DiscardActivityDialog(
                    onConfirmDiscard = onConfirmDiscard,
                    onDismissDialog = onDismissDialog
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    label = { Text(stringResource(id = R.string.title_label)) },
                    value = activityTitle,
                    onValueChange = onTitleChange
                )
                Spacer(Modifier.height(24.dp))
                DiscardActivityButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.End),
                    onClick = onDiscardClick,
                )
            }
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

@Composable
fun DiscardActivityButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error
        ),
        onClick = onClick
    ) {
        Text(stringResource(id = R.string.discard_activity))
    }
}

@Composable
fun DiscardActivityDialog(onConfirmDiscard: () -> Unit, onDismissDialog: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        confirmButton = {
            TextButton(onClick = onConfirmDiscard) {
                Text(stringResource(id = R.string.discard))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissDialog) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = { Text(stringResource(id = R.string.discard_warning)) },
        text = { Text(stringResource(id = R.string.discard_text)) }
    )
}

@Preview
@Composable
fun SaveRecordingScreenPreview() {
    ActivelyTheme {
        SaveRecordingScreen(
            activityTitle = "Morning activity",
            showDiscardWarningDialog = false,
            onTitleChange = {},
            onSaveClick = {},
            onDiscardClick = {},
            onConfirmDiscard = {},
            onDismissDialog = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
fun DiscardActivityDialogPreview() {
    ActivelyTheme {
        DiscardActivityDialog(onConfirmDiscard = {}, onDismissDialog = {})
    }
}
