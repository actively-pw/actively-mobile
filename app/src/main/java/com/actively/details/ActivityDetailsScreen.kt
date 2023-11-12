package com.actively.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.activityDetailsScreen(navController: NavController) {
    composable("activity_details_screen") {
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = {
                    TopAppBar(title = { Text(text = "Ride") })
                }
            ) {
                ActivityDetailsScreen()
            }
        }
    }
}

@Composable
fun ActivityDetailsScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            MapItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        }
    }
}

@Composable
private fun MapItem(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(color = MaterialTheme.colorScheme.primary)) {
    }
}
