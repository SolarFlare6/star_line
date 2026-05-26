package com.example.starline.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SpaceColorScheme = darkColorScheme(
    primary = NeonPrimary,
    secondary = NeonSecondary,
    tertiary = NeonTertiary,
    background = SpaceBackground,
    surface = SpaceSurface,
    surfaceVariant = SpaceSurfaceVariant,
    onPrimary = StarWhite,
    onSecondary = StarWhite,
    onTertiary = StarWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = SpaceBorder,
    error = Color(0xFFEF4444)
)

@Composable
fun StarLineTheme(
    content: @Composable () -> Unit
) {
    // Immersive experience: Locked to our curated space theme rather than browser/system defaults.
    MaterialTheme(
        colorScheme = SpaceColorScheme,
        typography = Typography,
        content = content
    )
}
