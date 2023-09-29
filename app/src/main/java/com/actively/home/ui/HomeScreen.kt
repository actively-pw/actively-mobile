package com.actively.home.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.actively.activity.RecordedActivity
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.homeScreen(navController: NavController) {
    composable("home_screen") {
        val viewModel: HomeViewModel = getViewModel()
        val activities = viewModel.activitiesPager.flow.collectAsLazyPagingItems()
        ActivelyTheme {
            BaseScaffoldScreen(navController = navController) {
                HomeScreen(activities = activities)
            }
        }

    }
}

@Composable
fun HomeScreen(
    activities: LazyPagingItems<RecordedActivity>,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
