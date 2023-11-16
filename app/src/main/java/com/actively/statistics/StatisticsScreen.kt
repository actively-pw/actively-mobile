package com.actively.statistics

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen

fun NavGraphBuilder.statisticsScreen(navController: NavController) {
    composable("statistics_screen") {
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = {}
            ) {
            }
        }
    }
}

@Composable
fun StatisticsScreen() {
}
