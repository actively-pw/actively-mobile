package com.actively.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                StatisticsScreen()
            }
        }
    }
}

@Composable
fun StatisticsScreen() {
    Column {
        val tabs = remember { listOf("Cycling", "Running", "Swimming", "Nordic walking") }
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = tab) }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvgWeeklyStats()
                YearToDateStats()
                AllTimeStats()
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun AvgWeeklyStats(modifier: Modifier = Modifier) {
    StatisticsContainer(modifier = modifier, title = "Avg weekly activity") {
        StatsRow(label = "Rides", value = "0")
        StatsRow(label = "Time", value = "0 h")
        StatsRow(label = "Distance", value = "0 km")
    }
}

@Composable
fun YearToDateStats(modifier: Modifier = Modifier) {
    StatisticsContainer(modifier = modifier, title = "Year-to-date") {
        StatsRow(label = "Rides", value = "0")
        StatsRow(label = "Time", value = "0 h")
        StatsRow(label = "Distance", value = "0 km")
        StatsRow(label = "Elevation gain", value = "0 m")
    }
}

@Composable
fun AllTimeStats(modifier: Modifier = Modifier) {
    StatisticsContainer(modifier = modifier, title = "All time") {
        StatsRow(label = "Rides", value = "0")
        StatsRow(label = "Distance", value = "0 km")
        StatsRow(label = "Longest ride", value = "0 km")
    }
}

@Composable
fun StatisticsContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = modifier) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            text = title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Divider(Modifier)
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

@Composable
fun StatsRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    ActivelyTheme {
        StatisticsScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun AvgWeekStatsPreview() {
    ActivelyTheme {
        AvgWeeklyStats()
    }
}

@Preview
@Composable
fun YearToDateStatsPreview() {
    ActivelyTheme {
        YearToDateStats()
    }
}

@Preview
@Composable
fun AllTimeStatsPreview() {
    ActivelyTheme {
        AllTimeStats()
    }
}
