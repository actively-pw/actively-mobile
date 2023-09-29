package com.actively.home.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.actively.activity.RecordedActivity
import com.actively.ui.theme.ActivelyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activities: LazyPagingItems<RecordedActivity>,
) {
    ActivelyTheme {
        Scaffold { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                items(count = activities.itemCount) { index ->
                    activities[index]?.let {
                        RecordedActivityItem(recordedActivity = it)
                        if (index != activities.itemCount - 1) {
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

