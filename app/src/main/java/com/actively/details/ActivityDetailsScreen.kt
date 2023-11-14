package com.actively.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.actively.home.ui.ErrorItem
import com.actively.recorder.ui.LabeledValue
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.activityDetailsScreen(navController: NavController) {
    composable("activity_details_screen") {
        val viewModel: ActivityDetailsViewModel = getViewModel()
        val state by viewModel.state.collectAsState()
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = { AppBar(state = state, onBackClick = { navController.popBackStack() }) }
            ) {
                ActivityDetailsScreen(state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(state: DetailsScreenState, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(if (state is DetailsScreenState.Loaded) state.typeOfActivity else "") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun ActivityDetailsScreen(state: DetailsScreenState) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        when (state) {
            DetailsScreenState.Loading -> loadingItem()
            is DetailsScreenState.Loaded -> loadedDetailsItem(state.imageUrl, state.details)
            DetailsScreenState.Error -> errorItem()
        }
    }
}

private fun LazyListScope.loadingItem() = item {
    Column(
        modifier = Modifier.fillParentMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

private fun LazyListScope.loadedDetailsItem(imageUrl: String, details: List<DetailsRow>) = item {
    AsyncImage(
        modifier = Modifier.fillMaxWidth(),
        model = imageUrl,
        contentDescription = null
    )
    Spacer(Modifier.height(4.dp))
    DetailsItem(details = details)
}

private fun LazyListScope.errorItem() = item {
    ErrorItem(modifier = Modifier.fillParentMaxSize())
}

@Composable
private fun DetailsItem(details: List<DetailsRow>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        details.forEach {
            DetailsRow(modifier = Modifier.fillMaxWidth(), row = it)
            Divider(Modifier.padding(4.dp))
        }
    }
}

@Composable
private fun DetailsRow(row: DetailsRow, modifier: Modifier = Modifier) {
    val (lLabel, lValue) = row.left
    val (rLabel, rValue) = row.right
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LabeledValue(modifier = Modifier.weight(1f), label = lLabel, value = lValue)
        Divider(
            Modifier
                .width(1.dp)
                .fillMaxHeight()
        )
        LabeledValue(
            modifier = Modifier.weight(1f),
            label = rLabel,
            value = rValue
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    ActivelyTheme {
        DetailsItem(
            modifier = Modifier.fillMaxWidth(),
            details = listOf(
                DetailsRow("Distance (km)" to "10.03", "Max speed (km/h)" to "30.41"),
                DetailsRow("Total time" to "01:20:59", "Sum of Ascent (m)" to "420"),
                DetailsRow("Avg speed (km/h)" to "20.59", "Sum of descent (m)" to "210"),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsRowPreview() {
    ActivelyTheme {
        DetailsRow(
            modifier = Modifier.fillMaxWidth(),
            row = DetailsRow("Distance (km)" to "10.03", "Max speed (km/h)" to "30.41")
        )
    }
}
