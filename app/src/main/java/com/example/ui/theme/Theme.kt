package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AderaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF064E3B), // Deep Forest Green Container
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = AderaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0F172A), // Dark Navy Container
    background = AderaBackgroundDark,
    surface = AderaSurfaceDark,
    surfaceVariant = AderaSurfaceVariantDark,
    onBackground = AderaTextMainDark,
    onSurface = AderaTextMainDark,
    onSurfaceVariant = AderaTextSubDark,
    outline = Color(0xFF475569), // Slate 600
    outlineVariant = Color(0xFF334155), // Slate 700
    error = AderaDanger
)

private val LightColorScheme = lightColorScheme(
    primary = AderaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5), // Light Mint Green Container
    onPrimaryContainer = Color(0xFF065F46), // Deep Forest Green Text
    secondary = AderaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0), // Clean light slate
    background = AderaBackgroundLight,
    surface = AderaSurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100
    onBackground = AderaTextMainLight,
    onSurface = AderaTextMainLight,
    onSurfaceVariant = AderaTextSubLight,
    outline = Color(0xFFCBD5E1), // Slate 300
    outlineVariant = Color(0xFFE2E8F0), // Slate 200
    error = AderaDanger
)

@Composable
fun AderaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
