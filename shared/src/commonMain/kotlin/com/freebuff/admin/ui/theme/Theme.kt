package com.freebuff.admin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object AppColors {
    val Blue = Color(0xFF007AFF)
    val Green = Color(0xFF34C759)
    val Orange = Color(0xFFFF9500)
    val Red = Color(0xFFFF3B30)
    val Amber = Color(0xFFFFCC00)
    val Purple = Color(0xFFAF52DE)
    val Gray100 = Color(0xFFAEAEB2)
    val Gray300 = Color(0xFFD1D1D6)
    val Gray400 = Color(0xFFE5E5EA)
    val Gray500 = Color(0xFF636366)
    val Gray600 = Color(0xFF48484A)
    val Gray700 = Color(0xFF3A3A3C)
    val Gray800 = Color(0xFF2C2C2E)
    val Gray900 = Color(0xFF1C1C1E)
    val Background = Color(0xFFF2F2F7)
    val Surface = Color(0xFFFFFFFF)
    val BackgroundDark = Color(0xFF000000)
    val SurfaceDark = Color(0xFF1C1C1E)
}

@Immutable
data class AppThemeColors(
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val mutedForeground: Color,
    val border: Color,
    val destructive: Color,
    val success: Color,
    val warning: Color,
    val card: Color,
    val inputBorder: Color,
    val surfaceVariant: Color,
    val inputBackground: Color,
)

val LightColors = AppThemeColors(
    background = AppColors.Background,
    surface = AppColors.Surface,
    onSurface = AppColors.Gray900,
    onSurfaceVariant = AppColors.Gray500,
    primary = AppColors.Blue,
    mutedForeground = AppColors.Gray500,
    border = AppColors.Gray300,
    destructive = AppColors.Red,
    success = AppColors.Green,
    warning = AppColors.Orange,
    card = AppColors.Surface,
    inputBorder = AppColors.Gray300,
    surfaceVariant = AppColors.Gray100,
    inputBackground = AppColors.Surface,
)

val DarkColors = AppThemeColors(
    background = AppColors.BackgroundDark,
    surface = AppColors.SurfaceDark,
    onSurface = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF98989D),
    primary = AppColors.Blue,
    mutedForeground = Color(0xFF98989D),
    border = AppColors.Gray700,
    destructive = AppColors.Red,
    success = AppColors.Green,
    warning = AppColors.Orange,
    card = AppColors.Gray800,
    inputBorder = AppColors.Gray700,
    surfaceVariant = AppColors.Gray700,
    inputBackground = AppColors.Gray800,
)

val LocalAppColors = staticCompositionLocalOf { LightColors }

object AppTheme {
    @Composable
    fun colors(): AppThemeColors = LocalAppColors.current
}

@Composable
fun FreebuffTheme(content: @Composable () -> Unit) {
    val colors = LightColors
    val colorScheme = lightColorScheme(
        primary = AppColors.Blue,
        secondary = AppColors.Blue,
        background = colors.background,
        surface = colors.surface,
        onBackground = colors.onSurface,
        onSurface = colors.onSurface,
    )
    androidx.compose.runtime.CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
