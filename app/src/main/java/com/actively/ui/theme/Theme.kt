package com.actively.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val lightColors = lightColorScheme(
    primary = LightColors.primary,
    onPrimary = LightColors.onPrimary,
    primaryContainer = LightColors.primaryContainer,
    onPrimaryContainer = LightColors.onPrimaryContainer,
    secondary = LightColors.secondary,
    onSecondary = LightColors.onSecondary,
    secondaryContainer = LightColors.secondaryContainer,
    onSecondaryContainer = LightColors.onSecondaryContainer,
    tertiary = LightColors.tertiary,
    onTertiary = LightColors.onTertiary,
    tertiaryContainer = LightColors.tertiaryContainer,
    onTertiaryContainer = LightColors.onTertiaryContainer,
    error = LightColors.error,
    errorContainer = LightColors.errorContainer,
    onError = LightColors.onError,
    onErrorContainer = LightColors.onErrorContainer,
    background = LightColors.background,
    onBackground = LightColors.onBackground,
    surface = LightColors.surface,
    onSurface = LightColors.onSurface,
    surfaceVariant = LightColors.surfaceVariant,
    onSurfaceVariant = LightColors.onSurfaceVariant,
    outline = LightColors.outline,
    inverseOnSurface = LightColors.inverseOnSurface,
    inverseSurface = LightColors.inverseSurface,
    inversePrimary = LightColors.inversePrimary,
    surfaceTint = LightColors.surfaceTint,
    outlineVariant = LightColors.outlineVariant,
    scrim = LightColors.scrim,
)

private val darkColors = darkColorScheme(
    primary = DarkColors.primary,
    onPrimary = DarkColors.onPrimary,
    primaryContainer = DarkColors.primaryContainer,
    onPrimaryContainer = DarkColors.onPrimaryContainer,
    secondary = DarkColors.secondary,
    onSecondary = DarkColors.onSecondary,
    secondaryContainer = DarkColors.secondaryContainer,
    onSecondaryContainer = DarkColors.onSecondaryContainer,
    tertiary = DarkColors.tertiary,
    onTertiary = DarkColors.onTertiary,
    tertiaryContainer = DarkColors.tertiaryContainer,
    onTertiaryContainer = DarkColors.onTertiaryContainer,
    error = DarkColors.error,
    errorContainer = DarkColors.errorContainer,
    onError = DarkColors.onError,
    onErrorContainer = DarkColors.onErrorContainer,
    background = DarkColors.background,
    onBackground = DarkColors.onBackground,
    surface = DarkColors.surface,
    onSurface = DarkColors.onSurface,
    surfaceVariant = DarkColors.surfaceVariant,
    onSurfaceVariant = DarkColors.onSurfaceVariant,
    outline = DarkColors.outline,
    inverseOnSurface = DarkColors.inverseOnSurface,
    inverseSurface = DarkColors.inverseSurface,
    inversePrimary = DarkColors.inversePrimary,
    surfaceTint = DarkColors.surfaceTint,
    outlineVariant = DarkColors.outlineVariant,
    scrim = DarkColors.scrim,
)

@Composable
fun ActivelyTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        lightColors
    } else {
        darkColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
