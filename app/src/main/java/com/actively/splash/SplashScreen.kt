package com.actively.splash

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.ui.theme.ActivelyTheme
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.splashScreen(navController: NavController) {
    composable("splash_screen") {
        val viewModel = getViewModel<SplashScreenViewModel>()
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()
        ActivelyTheme {
            LaunchedEffect(isLoggedIn) {
                when (isLoggedIn) {
                    null -> {}
                    true -> navController.navigate("home_screen")
                    false -> navController.navigate("welcome_screen")
                }
            }
        }
    }
}
