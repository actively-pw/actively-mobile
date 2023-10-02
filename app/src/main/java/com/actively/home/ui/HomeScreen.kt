package com.actively.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.actively.R
import com.actively.activity.RecordedActivity
import com.actively.synchronizer.WorkState
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeScreen(navController: NavController) {
    composable("home_screen") {
        val viewModel: HomeViewModel = getViewModel()
        val activities = viewModel.activitiesPager.flow.collectAsLazyPagingItems()
        val syncState by viewModel.syncState.collectAsState()
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = {
                    TopAppBar(title = { Text(stringResource(R.string.your_activities)) })
                }
            ) {
                HomeScreen(activities = activities, syncState)
            }
        }

    }
}

@Composable
fun HomeScreen(
    activities: LazyPagingItems<RecordedActivity>,
    syncState: WorkState?
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (activities.loadState.refresh == LoadState.Loading) {
            item {
                Column(
                    modifier = Modifier.fillParentMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            syncState?.let {
                if (it is WorkState.Running || it is WorkState.Enqueued) {
                    item {
                        SyncInProgressItem()
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
            items(count = activities.itemCount) { index ->
                activities[index]?.let {
                    RecordedActivityItem(recordedActivity = it)
                    if (index != activities.itemCount - 1) {
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
            if (activities.loadState.append == LoadState.Loading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
