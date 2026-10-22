package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color.Black,
    primaryContainer = CyanContainer,
    onPrimaryContainer = OnCyanContainer,
    secondary = AmberSecondary,
    onSecondary = Color.Black,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = VioletTertiary,
    background = MidnightDark,
    surface = MidnightCard,
    surfaceVariant = MidnightSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = MidnightBorder
)

private val OledColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003040),
    onPrimaryContainer = CyanPrimary,
    secondary = AmberSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF332200),
    onSecondaryContainer = AmberSecondary,
    tertiary = VioletTertiary,
    background = OledBlack,
    surface = OledCard,
    surfaceVariant = OledSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = OledBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00838F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = Color(0xFFF57F17),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF9C4),
    onSecondaryContainer = Color(0xFF5D4037),
    tertiary = VioletTertiary,
    background = LightBg,
    surface = LightCard,
    surfaceVariant = LightSurface,
    onBackground = TextDarkPrimary,
    onSurface = TextDarkPrimary,
    onSurfaceVariant = TextDarkSecondary,
    outline = LightBorder
)

@Composable
fun FotoVaultTheme(
    darkTheme: Boolean = true,
    oledBlack: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        oledBlack -> OledColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
