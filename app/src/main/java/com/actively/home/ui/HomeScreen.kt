package com.actively.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeScreen(navController: NavController) {
    composable("home_screen") {
        val viewModel: HomeViewModel = getViewModel()
        val activities = viewModel.activitiesPager.collectAsLazyPagingItems()
        val syncState by viewModel.syncState.collectAsState()
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.your_activities)) },
                        actions = {
                            TextButton(
                                onClick = {
                                    viewModel.onLogout { navController.navigate("splash_screen") }
                                }
                            ) {
                                Text(stringResource(id = R.string.logout))
                            }
                        })
                }
            ) {
                HomeScreen(
                    activities = activities,
                    syncState = syncState,
                    onNavigateToRecorder = { navController.navigate("recording_screen") },
                    onNavigateToDetails = { navController.navigate("activity_details_screen/${it.value}") }
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activities: LazyPagingItems<RecordedActivityUi>,
    syncState: WorkState?,
    onNavigateToRecorder: () -> Unit,
    onNavigateToDetails: (RecordedActivity.Id) -> Unit
) {
    val refreshState = rememberPullRefreshState(
        refreshing = activities.loadState.refresh == LoadState.Loading,
        onRefresh = activities::refresh
    )
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState),
        ) {
            syncState?.let {
                if (it is WorkState.Running || it is WorkState.Enqueued) {
                    syncInProgressItem()
                }
            }
            when {
                activities.loadState.refresh is LoadState.Error -> errorItem()
                activities.itemCount == 0 && activities.loadState.refresh != LoadState.Loading -> {
                    emptyListItem(onNavigateToRecorder = onNavigateToRecorder)
                }

                else -> activitiesItems(activities, onNavigateToDetails = onNavigateToDetails)
            }
            if (activities.loadState.append == LoadState.Loading) {
                appendItemsProgressIndicator()
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = activities.loadState.refresh == LoadState.Loading,
            state = refreshState
        )
    }
}

fun LazyListScope.syncInProgressItem() = item {
    SyncInProgressItem()
    Spacer(Modifier.height(6.dp))
}

fun LazyListScope.errorItem() = item {
    ErrorItem(
        modifier = Modifier
            .fillParentMaxSize()
            .padding(20.dp)
    )
}

fun LazyListScope.emptyListItem(onNavigateToRecorder: () -> Unit) = item {
    EmptyActivitiesListItem(
        modifier = Modifier
            .fillParentMaxSize()
            .padding(20.dp),
        onNavigateToRecorder = onNavigateToRecorder
    )
}

fun LazyListScope.activitiesItems(
    activities: LazyPagingItems<RecordedActivityUi>,
    onNavigateToDetails: (RecordedActivity.Id) -> Unit
) = items(count = activities.itemCount) { index ->
    activities[index]?.let {
        RecordedActivityItem(recordedActivity = it, onClick = { onNavigateToDetails(it.id) })
        if (index != activities.itemCount - 1) {
            Spacer(Modifier.height(6.dp))
        }
    }
}

fun LazyListScope.appendItemsProgressIndicator() = item {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}
