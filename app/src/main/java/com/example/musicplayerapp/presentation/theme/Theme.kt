package com.example.musicplayerapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MusicDarkColors = darkColorScheme(
    primary = PurpleGradientStart,
    secondary = AccentTeal,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun MusicPlayerAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MusicDarkColors,
        typography = MusicTypography,
        content = content
    )
}
