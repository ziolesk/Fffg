package com.example.beipoa.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = SurfaceCard,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,
    secondary = Navy900,
    onSecondary = SurfaceCard,
    secondaryContainer = Navy700,
    onSecondaryContainer = SurfaceCard,
    tertiary = OrangeDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = BorderLight,
    onSurfaceVariant = TextMuted,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = SurfaceCard,
    primaryContainer = Navy700,
    onPrimaryContainer = OrangeLight,
    secondary = OrangeBadge,
    onSecondary = Navy900,
    background = Navy900,
    onBackground = SurfaceCard,
    surface = Navy800,
    onSurface = SurfaceCard,
    surfaceVariant = Navy700,
    onSurfaceVariant = BorderLight,
    outline = Navy600
)

@Composable
fun BeiPoaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Navy900.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
