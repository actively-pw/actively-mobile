package com.actively

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.actively.auth.ui.login.loginScreen
import com.actively.auth.ui.register.registerScreen
import com.actively.home.ui.homeScreen
import com.actively.recorder.ui.recorderScreen
import com.actively.recorder.ui.saveRecordingScreen
import com.actively.splash.splashScreen
import com.actively.welcome.ui.welcomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "splash_screen",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) {
                splashScreen(navController)
                navigation(startDestination = "welcome_screen", route = "auth_screens") {
                    welcomeScreen(navController)
                    loginScreen(navController)
                    registerScreen(navController)
                }
                navigation(startDestination = "home_screen", route = "authenticated_screens") {
                    homeScreen(navController)
                    recorderScreen(navController)
                    saveRecordingScreen(navController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val scope by inject<CoroutineScope>()
        scope.cancel()
    }
}
