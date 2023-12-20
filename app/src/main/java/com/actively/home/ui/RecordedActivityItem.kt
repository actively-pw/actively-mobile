package com.actively.home.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.actively.R
import com.actively.activity.Activity
import com.actively.activity.RecordedActivity
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.kilometers
import com.actively.ui.theme.ActivelyTheme
import com.actively.util.RecordedTimeText
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordedActivityItem(
    recordedActivity: RecordedActivityUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, onClick = onClick) {
        Column(modifier = Modifier.padding(12.dp)) {
            RecordedTimeText(recordedActivity.time, style = MaterialTheme.typography.bodySmall)
            Text(recordedActivity.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            StatisticsRow(modifier = Modifier.fillMaxWidth(), stats = recordedActivity.stats)
        }
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = recordedActivity.mapUrl,
            contentDescription = null,
            error = painterResource(id = R.drawable.placeholder_image)
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
            label = stringResource(R.string.distance),
            value = String.format("%.2f km", stats.distance.inKilometers)
        )
        Divider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
        )
        LabeledStat(
            label = stringResource(R.string.time),
            value = String.format(
                "%d:%02d h",
                stats.totalTime.inWholeHours,
                stats.totalTime.inWholeMinutes % 60
            )
        )
        Divider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
        )
        LabeledStat(
            label = stringResource(R.string.avg_speed),
            value = String.format("%.2f km/h", stats.averageSpeed)
        )
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
            onClick = {},
            recordedActivity = RecordedActivityUi(
                id = RecordedActivity.Id("1"),
                title = "Morning activity",
                time = RecordedActivityTime(
                    time = "10:15",
                    prefix = TimePrefix.Date("13 November 2023")
                ),
                stats = Activity.Stats(
                    distance = 21.5.kilometers,
                    totalTime = 1.5.hours,
                    averageSpeed = 15.0,
                ),
                mapUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT1OPqbJf7_yYS3uYolCHNavStsb0p-5xA8JgTBp-3eFA&s"
            )
        )
    }
}
