package com.smad.nutribalance.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Green800,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Teal700,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Teal200,
    onSecondaryContainer = Green900,
    background = Surface,
    onBackground = Green900,
    surface = Surface,
    onSurface = Green900,
    surfaceVariant = Green50,
    error = Red400,
    tertiary = Orange400,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green400,
    onPrimary = Green900,
    primaryContainer = Green800,
    onPrimaryContainer = Green100,
    secondary = Teal200,
    onSecondary = Green900,
    secondaryContainer = Teal700,
    onSecondaryContainer = Teal200,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = CardDark,
    error = Red400,
    tertiary = Orange400,
)

@Composable
fun NutriBalanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NutriTypography,
        content = content
    )
}