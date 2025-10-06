package com.example.inventariotds.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulOscuro,
    secondary = Amarillo,
    background = AzulOscuro,
    surface = Blanco,
    onPrimary = Blanco,
    onSecondary = AzulOscuro,
    onBackground = Blanco,
    onSurface = AzulOscuro
)

@Composable
fun InventarioTDSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
