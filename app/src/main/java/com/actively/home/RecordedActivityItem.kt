package com.actively.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.actively.activity.Activity
import com.actively.activity.RecordedActivity
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.kilometers
import com.actively.ui.theme.ActivelyTheme
import kotlin.time.Duration.Companion.hours

@Composable
fun RecordedActivityItem(
    recordedActivity: RecordedActivity,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(recordedActivity.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            StatisticsRow(modifier = Modifier.fillMaxWidth(), stats = recordedActivity.stats)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun StatisticsRow(stats: Activity.Stats, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabeledStat(
            label = "Distance",
            value = String.format("%.2f km", stats.distance.inKilometers)
        )
        Divider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
        )
        LabeledStat(
            label = "Time",
            value = String.format(
                "%d:%2d h",
                stats.totalTime.inWholeHours,
                stats.totalTime.inWholeMinutes % 60
            )
        )
        Divider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
        )
        LabeledStat(label = "Avg", value = String.format("%.2f km/h", stats.averageSpeed))
    }
}

@Composable
fun LabeledStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light)
        )
        Text(text = value, style = MaterialTheme.typography.headlineSmall)
    }
}


@Preview(showBackground = true)
@Composable
fun RecordedActivityItemPreview() {
    ActivelyTheme {
        RecordedActivityItem(
            recordedActivity = RecordedActivity(
                id = RecordedActivity.Id("1"),
                title = "Morning activity",
                sport = "Cycling",
                stats = Activity.Stats(
                    distance = 21.5.kilometers,
                    totalTime = 1.5.hours,
                    averageSpeed = 15.0,
                ),
                routeUrl = "url://actively.webservice.net"
            )
        )
    }
}
