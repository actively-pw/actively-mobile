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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.actively.R
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
                topBar = {
                    AppBar(
                        state = state,
                        onBackClick = { navController.popBackStack() },
                        onDeleteClick = {}
                    )
                }
            ) {
                ActivityDetailsScreen(state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(state: DetailsScreenState, onBackClick: () -> Unit, onDeleteClick: () -> Unit) {
    TopAppBar(
        title = { Text(if (state is DetailsScreenState.Loaded) state.typeOfActivity else "") },
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
fun ActivityDetailsScreen(state: DetailsScreenState) {
    if (state is DetailsScreenState.Loaded && state.showConfirmDeleteDialog) {
        DeleteActivityDialog(onConfirm = {}, onDismiss = {})
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        when (state) {
            DetailsScreenState.Loading -> loadingItem()
            is DetailsScreenState.Loaded -> loadedDetailsItem(state)
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

private fun LazyListScope.loadedDetailsItem(state: DetailsScreenState.Loaded) =
    item {
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(top = 4.dp),
            text = state.time,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Light)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 4.dp),
            text = state.title,
            style = MaterialTheme.typography.titleLarge,
        )
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = state.imageUrl,
            contentDescription = null
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
            TextButton(onClick = onConfirm) {
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
