package com.actively.details

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
    
}
