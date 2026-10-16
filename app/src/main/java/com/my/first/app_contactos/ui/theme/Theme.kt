package com.my.first.app_contactos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = celeste,
    secondary = Gris,
    background = Gris,
    surface = Gris,
    onPrimary = blanco,
    onSecondary = blanco,
    onBackground = blanco,
    onSurface = blanco,
    onSurfaceVariant = blanco,
    outline = blanco
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
