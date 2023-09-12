package com.actively.recorder.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier
            .wrapContentSize()
            .size(size),
        shape = CircleShape,
        contentPadding = PaddingValues(4.dp),
        onClick = onClick,
        enabled = enabled,
        content = content
    )
}

@Composable
fun OutlinedRoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 80.dp,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        modifier = modifier
            .wrapContentSize()
            .size(size),
        shape = CircleShape,
        contentPadding = PaddingValues(4.dp),
        onClick = onClick,
        enabled = enabled,
        content = content
    )
}
