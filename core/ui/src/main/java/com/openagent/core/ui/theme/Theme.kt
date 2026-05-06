package com.openagent.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// OpenClaw 蓝色系
val OpenClawBlue = Color(0xFF1976D2)
val OpenClawBlueDark = Color(0xFF0D47A1)
val OpenClawBlueLight = Color(0xFF42A5F5)

// Hermes 紫色系
val HermesPurple = Color(0xFF7B1FA2)
val HermesPurpleDark = Color(0xFF4A148C)
val HermesPurpleLight = Color(0xFFAB47BC)

// 状态色
val StatusGreen = Color(0xFF4CAF50)
val StatusRed = Color(0xFFF44336)
val StatusOrange = Color(0xFFFF9800)
val StatusGray = Color(0xFF9E9E9E)

private val LightColorScheme = lightColorScheme(
    primary = OpenClawBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = OpenClawBlueDark,
    secondary = OpenClawBlueLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = Color(0xFF1565C0),
    tertiary = HermesPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE1BEE7),
    onTertiaryContainer = HermesPurpleDark,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    error = StatusRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = OpenClawBlueDark,
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF64B5F6),
    onSecondary = OpenClawBlueDark,
    secondaryContainer = OpenClawBlue,
    onSecondaryContainer = Color(0xFFE3F2FD),
    tertiary = Color(0xFFCE93D8),
    onTertiary = HermesPurpleDark,
    tertiaryContainer = HermesPurple,
    onTertiaryContainer = Color(0xFFE1BEE7),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFEF5350),
    onError = Color(0xFF601410)
)

@Composable
fun OpenAgentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
        typography = Typography(),
        content = content
    )
}
