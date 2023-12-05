package com.actively.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.R
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.statisticsScreen(navController: NavController) {
    composable("statistics_screen") {
        val viewModel = getViewModel<StatisticsViewModel>()
        val state by viewModel.state.collectAsState()
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
                StatisticsScreen(state, viewModel::onSelectTab)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticsScreen(state: StatisticsState, onSelectTab: (Int) -> Unit) {
    Column {
        val pagerState = rememberPagerState(pageCount = state.tabs::size)
        val scope = rememberCoroutineScope()
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onSelectTab(page)
            }
        }
        ScrollableTabRow(selectedTabIndex = state.selectedTab) {
            state.tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = index == state.selectedTab,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(page = index)
                        }
                    },
                    text = { Text(text = stringResource(id = tab.sport)) }
                )
            }
        }
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            HorizontalPager(state = pagerState) {
                val page = state.tabs[it]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticsContainer(
                        title = stringResource(id = R.string.avg_weekly_activity),
                        stats = page.avgWeekly
                    )
                    StatisticsContainer(
                        title = stringResource(id = R.string.year_to_date),
                        stats = page.yearToDate
                    )
                    StatisticsContainer(
                        title = stringResource(id = R.string.all_time),
                        stats = page.allTime
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsContainer(
    title: String,
    stats: List<LabeledValue>,
    modifier: Modifier = Modifier,
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
            stats.forEach {
                StatsRow(label = stringResource(it.label), value = it.value)
            }
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
        StatisticsScreen(
            state = StatisticsState(
                tabs = listOf(
                    StatTab(
                        sport = R.string.cycling,
                        avgWeekly = listOf(
                            LabeledValue(label = R.string.rides, value = "0"),
                            LabeledValue(label = R.string.time, value = "0 h"),
                            LabeledValue(label = R.string.distance, value = "0 km")
                        ),
                        yearToDate = listOf(
                            LabeledValue(label = R.string.rides, value = "0"),
                            LabeledValue(label = R.string.time, value = "0 h"),
                            LabeledValue(label = R.string.distance, value = "0 km"),
                            LabeledValue(label = R.string.elevation_gain, value = "0 km")
                        ),
                        allTime = listOf(
                            LabeledValue(label = R.string.rides, value = "0"),
                            LabeledValue(label = R.string.time, value = "0 h"),
                            LabeledValue(label = R.string.longest_ride, value = "0 km")
                        )
                    ),
                    StatTab(sport = R.string.running),
                    StatTab(sport = R.string.nordic_waling),
                ),
            ),
            onSelectTab = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsContainerPreview() {
    ActivelyTheme {
        StatisticsContainer(
            title = stringResource(id = R.string.avg_weekly_activity),
            stats = listOf(
                LabeledValue(label = R.string.rides, value = "0"),
                LabeledValue(label = R.string.time, value = "0 h"),
                LabeledValue(label = R.string.distance, value = "0 km")
            )
        )
    }
}
