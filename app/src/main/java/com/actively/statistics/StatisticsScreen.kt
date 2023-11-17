package com.actively.statistics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.statisticsScreen(navController: NavController) {
    composable("statistics_screen") {
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = {
                    TopAppBar(
                        title = { Text("Statistics") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                        }
                    )
                }
            ) {
            }
        }
    }
}

@Composable
fun StatisticsScreen() {
}
