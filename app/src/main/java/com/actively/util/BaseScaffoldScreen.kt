package com.actively.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.actively.R
import com.actively.navigation.BottomBar
import com.actively.navigation.BottomBarItem

@Composable
fun BaseScaffoldScreen(
    navController: NavController,
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = {
            val items = listOf(
                BottomBarItem(R.string.home, R.drawable.home, route = "home_screen"),
                BottomBarItem(R.string.recorder, R.drawable.record, route = "recording_screen"),
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
        Column(modifier = Modifier.padding(it)) {
            content()
        }
    }
}
