package com.dkwin.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DkBackground = Color(0xFF070B14)
val DkSurface = Color(0xFF111827)
val DkSurfaceVariant = Color(0xFF1E293B)
val DkGold = Color(0xFFF59E0B)
val DkGoldLight = Color(0xFFFBBF24)
val DkEmerald = Color(0xFF10B981)
val DkCrimson = Color(0xFFEF4444)
val DkViolet = Color(0xFF8B5CF6)
val DkSky = Color(0xFF0EA5E9)
val DkTextPrimary = Color(0xFFF8FAFC)
val DkTextSecondary = Color(0xFF94A3B8)

private val DarkColorScheme = darkColorScheme(
    primary = DkGold,
    onPrimary = Color(0xFF090D16),
    secondary = DkEmerald,
    onSecondary = Color(0xFF052E16),
    tertiary = DkViolet,
    background = DkBackground,
    onBackground = DkTextPrimary,
    surface = DkSurface,
    onSurface = DkTextPrimary,
    surfaceVariant = DkSurfaceVariant,
    onSurfaceVariant = DkTextSecondary,
    error = DkCrimson
)

@Composable
fun DkwinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
