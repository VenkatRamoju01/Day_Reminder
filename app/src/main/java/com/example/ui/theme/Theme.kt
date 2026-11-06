package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DayZeroAccentWhite,
    onPrimary = DayZeroBlack,
    primaryContainer = DayZeroSurfaceVariant,
    onPrimaryContainer = DayZeroTextPrimary,
    secondary = DayZeroTextSecondary,
    onSecondary = DayZeroBlack,
    background = DayZeroBlack,
    onBackground = DayZeroTextPrimary,
    surface = DayZeroDarkCharcoal,
    onSurface = DayZeroTextPrimary,
    surfaceVariant = DayZeroSurface,
    onSurfaceVariant = DayZeroTextSecondary,
    outline = DayZeroBorder,
    outlineVariant = DayZeroBorder
)

private val LightColorScheme = lightColorScheme(
    primary = DayZeroBlack,
    onPrimary = DayZeroAccentWhite,
    primaryContainer = WarmOffWhiteSurface,
    onPrimaryContainer = WarmOffWhiteText,
    secondary = DayZeroTextMuted,
    onSecondary = DayZeroAccentWhite,
    background = WarmOffWhiteBg,
    onBackground = WarmOffWhiteText,
    surface = WarmOffWhiteBg,
    onSurface = WarmOffWhiteText,
    surfaceVariant = WarmOffWhiteSurface,
    onSurfaceVariant = WarmOffWhiteText,
    outline = DayZeroBorder,
    outlineVariant = DayZeroBorder
)

@Composable
fun DayZeroTheme(
    darkTheme: Boolean = true, // Default to premium dark aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
