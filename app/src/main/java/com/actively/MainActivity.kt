package com.actively

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.actively.home.ui.homeScreen
import com.actively.recorder.ui.recorderScreen
import com.actively.recorder.ui.saveRecordingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "home_screen",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) {
                homeScreen(navController)
                recorderScreen(navController)
                saveRecordingScreen(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScaffoldScreen(navController: NavController, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        bottomBar = {
            val items = listOf(
                BottomBarItem(R.string.home, Icons.Default.Home, route = "home_screen"),
                BottomBarItem(R.string.recorder, Icons.Default.Add, route = "recording_screen"),
            )
            var selectedItem by remember {
                mutableStateOf(items.first())
            }
            val navStackState by navController.currentBackStackEntryAsState()
            LaunchedEffect(navStackState) {
                navStackState?.destination?.route?.let { currentRoute ->
                    items.find { it.route == currentRoute }?.let { selectedItem = it }
                }
            }
            BottomBar(
                items = items,
                selectedItem = selectedItem,
                onItemClick = { navController.navigate(it.route) }
            )
        }
    ) {
        content(it)
    }
}

@Composable
fun BottomBar(
    items: List<BottomBarItem>,
    onItemClick: (BottomBarItem) -> Unit,
    selectedItem: BottomBarItem,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item == selectedItem,
                onClick = { onItemClick(item) },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(stringResource(id = item.stringResource)) }
            )
        }
    }
}

data class BottomBarItem(val stringResource: Int, val icon: ImageVector, val route: String)
