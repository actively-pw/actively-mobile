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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.actively.recorder.ui.LabeledValue
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.BaseScaffoldScreen

fun NavGraphBuilder.activityDetailsScreen(navController: NavController) {
    composable("activity_details_screen") {
        ActivelyTheme {
            BaseScaffoldScreen(
                navController = navController,
                topBar = { AppBar(title = "Ride", onBackClick = { navController.popBackStack() }) }
            ) {
                ActivityDetailsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun ActivityDetailsScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            AsyncImage(modifier = Modifier.fillMaxWidth(), model = "", contentDescription = null)
            Spacer(Modifier.height(4.dp))
            DetailsItem()
        }
    }
}

@Composable
private fun DetailsItem(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LabeledValue(modifier = Modifier.weight(1f), label = "Distance (km)", value = "10.03")
            Divider(
                Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            LabeledValue(
                modifier = Modifier.weight(1f),
                label = "Max speed (km/h)",
                value = "30.41"
            )
        }
        Divider(Modifier.padding(vertical = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LabeledValue(modifier = Modifier.weight(1f), label = "Total time", value = "01:20:59")
            Divider(
                Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            LabeledValue(modifier = Modifier.weight(1f), label = "Sum of ascent (m)", value = "420")
        }
        Divider(Modifier.padding(vertical = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LabeledValue(
                modifier = Modifier.weight(1f),
                label = "Avg speed (km/h)",
                value = "20.59"
            )
            Divider(
                Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            LabeledValue(
                modifier = Modifier.weight(1f),
                label = "Sum of descent (m)",
                value = "210"
            )
        }
        Divider(Modifier.padding(vertical = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    ActivelyTheme {
        DetailsItem(modifier = Modifier.fillMaxWidth())
    }
}
