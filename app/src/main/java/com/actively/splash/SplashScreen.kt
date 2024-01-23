package com.actively.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    true -> navController.navigate("authenticated_screens")
                    false -> navController.navigate("auth_screens")
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
