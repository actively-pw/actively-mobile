package com.actively.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

data class BottomBarItem(
    @StringRes val id: Int,
    @DrawableRes val drawableId: Int,
    val route: String
)

@Composable
fun BottomBar(
    items: List<BottomBarItem>,
    onItemClick: (BottomBarItem) -> Unit,
    selectedItem: BottomBarItem,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item == selectedItem,
                onClick = { onItemClick(item) },
                icon = { Icon(painterResource(id = item.drawableId), contentDescription = null) },
                label = { Text(stringResource(id = item.id)) }
            )
        }
    }
}

