package com.example.tabapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = SamsungBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6EBFA),
    onPrimaryContainer = SamsungBlueDeep,
    secondary = Color(0xFF4A5670),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9ECF2),
    onSecondaryContainer = Color(0xFF1B2433),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8F9FA),
    surfaceContainer = Color(0xFFF2F3F5),
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color(0xFFE9EBEE),
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = Color(0xFFD93025),
    onError = Color.White,
    errorContainer = Color(0xFFFCE8E6),
    onErrorContainer = Color(0xFF8C1D18),
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF0A1A66),
    primaryContainer = Color(0xFF1E3190),
    onPrimaryContainer = Color(0xFFDDE4FF),
    secondary = Color(0xFFB9C3D8),
    onSecondary = Color(0xFF232C3D),
    secondaryContainer = Color(0xFF2E3748),
    onSecondaryContainer = Color(0xFFDDE3F0),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF23272E),
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = Color(0xFF0B0D10),
    surfaceContainerLow = Color(0xFF15181C),
    surfaceContainer = Color(0xFF1C1F24),
    surfaceContainerHigh = Color(0xFF22262C),
    surfaceContainerHighest = Color(0xFF2A2E35),
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = Color(0xFFF28B82),
    onError = Color(0xFF601410),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun TabAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
