package com.my.first.app_contactos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = celeste,
    secondary = Gris,
    background = Color(0xFFF4F6F8),
    surface = blanco,
    onPrimary = blanco,
    onSecondary = blanco,
    onBackground = negro,
    onSurface = negro,
    onSurfaceVariant = Color.Gray,
    outline = Gris
)

private val DarkColorScheme = darkColorScheme(
    primary = celeste,
    secondary = Gris,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = blanco,
    onSecondary = blanco,
    onBackground = blanco,
    onSurface = blanco,
    onSurfaceVariant = Color.LightGray,
    outline = blanco
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
