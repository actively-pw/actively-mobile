package com.actively

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.actively.recorder.ui.recorderScreen
import com.actively.recorder.ui.saveRecordingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject

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

    override fun onDestroy() {
        super.onDestroy()
        val scope by inject<CoroutineScope>()
        scope.cancel()
    }
}
