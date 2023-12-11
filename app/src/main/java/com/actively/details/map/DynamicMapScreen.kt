package com.actively.details.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.map.RouteMap
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.dynamicMapScreen(navController: NavController) {
    composable("activity_details_screen/{activityId}/map") {
        val viewModel: DynamicMapViewModel = getViewModel {
            parametersOf(it.arguments?.getString("activityId")!!)
        }
        val state by viewModel.state.collectAsState()
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = { AppBar(onBackClick = navController::popBackStack) }
            ) {
                DynamicMapScreen(state = state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Route") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun DynamicMapScreen(state: DynamicMapState) {
    RouteMap(modifier = Modifier.fillMaxSize(), routeGeoJson = state.routeUrl)
}
