package com.example.tabapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF1E4D8C)
val NavyDark = Color(0xFF12335E)
val SkyBlue = Color(0xFF9CC3F5)

private val LightColors = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FA),
    onPrimaryContainer = NavyDark,
    background = Color(0xFFF5F7FB),
    surface = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = SkyBlue,
    onPrimary = NavyDark,
    primaryContainer = Navy,
    onPrimaryContainer = Color.White,
)

@Composable
fun TabAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
