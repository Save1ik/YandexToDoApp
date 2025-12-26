package com.example.yandextodo.ui.theme

import android.app.Activity

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFDC143C),       // Яркий алый
    onPrimary = Color.White,           // Белый текст на алом
    primaryContainer = Color(0xFFFFCDD2), // Светло-розовый контейнер
    onPrimaryContainer = Color(0xFF8B0000), // Темно-красный текст в контейнере

    secondary = Color(0xFF8B0000),     // Темно-красный
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0E0),
    onSecondaryContainer = Color(0xFF5A0000),

    tertiary = Color(0xFFA52A2A),      // Коричнево-красный
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDADA),
    onTertiaryContainer = Color(0xFF6B0000),

    background = Color(0xFFFFFBFF),    // Чисто белый фон
    onBackground = Color(0xFF1C1B1F),  // Темно-серый текст

    surface = Color(0xFFFFFBFF),       // Белая поверхность
    onSurface = Color(0xFF1C1B1F),     // Темно-серый текст на поверхности

    surfaceVariant = Color(0xFFF5F5F5), // Светло-серый вариант поверхности
    onSurfaceVariant = Color(0xFF49454F), // Серый текст

    outline = Color(0xFF79747E),       // Серый контур
    outlineVariant = Color(0xFFC4C7C5) // Светло-серый вариант контура
)




@Composable
fun YandexToDoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}