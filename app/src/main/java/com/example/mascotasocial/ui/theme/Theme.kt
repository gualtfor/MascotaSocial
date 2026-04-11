package com.example.mascotasocial.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF964300),
    secondary = Color(0xFF005DA7),
    tertiary = Color(0xFF745700),
    background = Color(0xFFF5F6F7),
    surface = Color(0xFFF5F6F7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF2C2F30),
    onSurface = Color(0xFF2C2F30),
)

@Composable
fun MascotaSocialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
