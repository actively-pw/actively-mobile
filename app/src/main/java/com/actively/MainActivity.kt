package com.actively

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.actively.recorder.ui.recorderScreen
import com.actively.recorder.ui.saveRecordingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "recording_screen") {
                recorderScreen(navController)
                saveRecordingScreen(navController)
            }
        }
    }
}
