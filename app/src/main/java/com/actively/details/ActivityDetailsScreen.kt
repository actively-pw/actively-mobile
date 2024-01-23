package com.actively.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.actively.R
import com.actively.home.ui.ErrorItem
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen
import com.actively.util.RecordedTimeText
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.activityDetailsScreen(navController: NavController) {
    composable("activity_details_screen/{activityId}") {
        val activityId = it.arguments?.getString("activityId")!!
        val viewModel: ActivityDetailsViewModel = getViewModel {
            parametersOf(activityId)
        }
        val state by viewModel.state.collectAsState()
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = {
                    AppBar(
                        state = state,
                        onBackClick = { navController.popBackStack() },
                        onDeleteClick = viewModel::onDeleteClick
                    )
                }
            ) {
                ActivityDetailsScreen(
                    state = state,
                    onConfirmDelete = viewModel::onConfirmDelete,
                    onDiscard = viewModel::onDiscardDialogue,
                    navigateBack = navController::popBackStack,
                    onNavigateToDynamicMap = { navController.navigate("activity_details_map/${activityId}") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(state: DetailsScreenState, onBackClick: () -> Unit, onDeleteClick: () -> Unit) {
    TopAppBar(
        title = { Text(if (state is DetailsScreenState.Loaded) stringResource(state.sport) else "") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            if (state is DetailsScreenState.Loaded) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Composable
fun ActivityDetailsScreen(
    state: DetailsScreenState,
    onConfirmDelete: () -> Unit,
    onDiscard: () -> Unit,
    navigateBack: () -> Unit,
    onNavigateToDynamicMap: () -> Unit
) {
    if (state is DetailsScreenState.Loaded && state.showConfirmDeleteDialog) {
        DeleteActivityDialog(
            onConfirm = {
                onConfirmDelete()
                navigateBack()
            },
            onDismiss = onDiscard
        )
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        when (state) {
            DetailsScreenState.Loading -> loadingItem()
            is DetailsScreenState.Loaded -> loadedDetailsItem(state, onNavigateToDynamicMap)
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

private fun LazyListScope.loadedDetailsItem(
    state: DetailsScreenState.Loaded,
    onNavigateToDynamicMap: () -> Unit
) = item {
    RecordedTimeText(
        state.time,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 4.dp),
        style = MaterialTheme.typography.bodySmall
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 4.dp),
        text = state.title,
        style = MaterialTheme.typography.headlineMedium,
    )
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigateToDynamicMap),
        model = if (isSystemInDarkTheme()) state.darkMapUrl else state.lightMapUrl,
        contentDescription = null,
        error = painterResource(id = R.drawable.placeholder_image)
    )
    Spacer(Modifier.height(4.dp))
    DetailsItem(details = state.details)
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
        LabeledValue(modifier = Modifier.weight(1f), label = stringResource(lLabel), value = lValue)
        Divider(
            Modifier
                .width(1.dp)
                .fillMaxHeight()
        )
        LabeledValue(modifier = Modifier.weight(1f), label = stringResource(rLabel), value = rValue)
    }
}

@Composable
private fun LabeledValue(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelMedium)
        Text(text = value.uppercase(), style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun DeleteActivityDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(id = R.string.delete_activity))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = { Text(stringResource(id = R.string.delete_warning)) },
        text = { Text(stringResource(id = R.string.delete_text)) }
    )
}

@Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    ActivelyTheme {
        DetailsItem(
            modifier = Modifier.fillMaxWidth(),
            details = listOf(
                DetailsRow(R.string.distance_km to "10.03", R.string.max_speed_kmph to "30.41"),
                DetailsRow(R.string.time to "01:20:59", R.string.sum_ascent_meters to "420"),
                DetailsRow(
                    R.string.avg_speed_km_h to "20.59",
                    R.string.sum_descent_meters to "210"
                ),
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
            row = DetailsRow(R.string.distance_km to "10.03", R.string.max_speed_kmph to "30.41")
        )
    }
}
