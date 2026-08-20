package com.freebuff.admin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object AppColors {
    val Blue = Color(0xFF007AFF)
    val BlueLight = Color(0xFF5AC8FA)
    val Green = Color(0xFF34C759)
    val Orange = Color(0xFFFF9500)
    val Amber = Color(0xFFFFCC00)
    val Red = Color(0xFFFF3B30)
    val Purple = Color(0xFFAF52DE)
    val Pink = Color(0xFFFF2D55)

    // Gray scale
    val Gray50 = Color(0xFF8E8E93)
    val Gray100 = Color(0xFFAEAEB2)
    val Gray200 = Color(0xFFC7C7CC)
    val Gray300 = Color(0xFFD1D1D6)
    val Gray400 = Color(0xFFE5E5EA)
    val Gray500 = Color(0xFF636366)
    val Gray600 = Color(0xFF48484A)
    val Gray700 = Color(0xFF3A3A3C)
    val Gray800 = Color(0xFF2C2C2E)
    val Gray900 = Color(0xFF1C1C1E)

    // Semantic
    val Info = Blue
    val Warning = Orange
    val Danger = Red
    val Success = Green
    val Muted = Gray500
}

data class AppThemeColors(
    val background: Color,
    val surface: Color,
    val card: Color,
    val cardBorder: Color,
    val primary: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val onPrimary: Color,
    val border: Color,
    val borderSubtle: Color,
    val surfaceVariant: Color,
    val inputBackground: Color,
    val inputBorder: Color,
    val mutedForeground: Color,
    val destructive: Color,
    val success: Color,
    val warning: Color,
)

val LightColors = AppThemeColors(
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    card = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFE5E5EA),
    primary = AppColors.Blue,
    onSurface = Color(0xFF000000),
    onSurfaceVariant = Color(0xFF636366),
    onPrimary = Color(0xFFFFFFFF),
    border = Color(0xFFD1D1D6),
    borderSubtle = Color(0xFFE5E5EA),
    surfaceVariant = Color(0xFFF2F2F7),
    inputBackground = Color(0xFFFFFFFF),
    inputBorder = Color(0xFFD1D1D6),
    mutedForeground = Color(0xFF8E8E93),
    destructive = AppColors.Red,
    success = AppColors.Green,
    warning = AppColors.Orange,
)

val DarkColors = AppThemeColors(
    background = Color(0xFF000000),
    surface = Color(0xFF1C1C1E),
    card = Color(0xFF1C1C1E),
    cardBorder = Color(0xFF38383A),
    primary = AppColors.Blue,
    onSurface = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF8E8E93),
    onPrimary = Color(0xFFFFFFFF),
    border = Color(0xFF38383A),
    borderSubtle = Color(0xFF2C2C2E),
    surfaceVariant = Color(0xFF2C2C2E),
    inputBackground = Color(0xFF2C2C2E),
    inputBorder = Color(0xFF38383A),
    mutedForeground = Color(0xFF636366),
    destructive = AppColors.Red,
    success = AppColors.Green,
    warning = AppColors.Orange,
)

val LocalAppColors = staticCompositionLocalOf { LightColors }

object AppTheme {
    @Composable
    fun colors(darkTheme: Boolean = isSystemInDarkTheme()): AppThemeColors {
        return if (darkTheme) DarkColors else LightColors
    }
}

@Composable
fun FreebuffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            surface = colors.surface,
            onSurface = colors.onSurface,
            background = colors.background,
            onBackground = colors.onSurface,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.onSurfaceVariant,
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            surface = colors.surface,
            onSurface = colors.onSurface,
            background = colors.background,
            onBackground = colors.onSurface,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.onSurfaceVariant,
        )
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(colorScheme = colorScheme) {
            content()
        }
    }
}
