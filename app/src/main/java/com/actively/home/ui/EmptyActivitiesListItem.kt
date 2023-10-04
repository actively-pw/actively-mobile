package com.actively.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.actively.R
import com.actively.ui.theme.ActivelyTheme

@Composable
fun EmptyActivitiesListItem(onNavigateToRecorder: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.no_activities_home_screen_state),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToRecorder) {
            Text(stringResource(R.string.record_your_first_activity))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyActivitiesListItemPreview() {
    ActivelyTheme {
        EmptyActivitiesListItem(
            onNavigateToRecorder = { },
            modifier = Modifier.fillMaxSize()
        )
    }
}
