package com.actively.util

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.actively.R
import com.actively.home.ui.RecordedActivityTime
import com.actively.home.ui.TimePrefix

@Composable
fun RecordedTimeText(
    recordedActivityTime: RecordedActivityTime,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = when (recordedActivityTime.prefix) {
            is TimePrefix.Today -> stringResource(R.string.today_at, recordedActivityTime.time)
            is TimePrefix.Yesterday -> stringResource(
                R.string.yesterday_at,
                recordedActivityTime.time
            )

            is TimePrefix.Date -> stringResource(
                R.string.recorded_date_at,
                recordedActivityTime.prefix.value,
                recordedActivityTime.time
            )
        },
        style = style
    )
}
