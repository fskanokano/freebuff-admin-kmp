package com.freebuff.admin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Apple + shadcn color palette
object AppColors {
    // Primary
    val Blue = Color(0xFF007AFF)
    val BlueDark = Color(0xFF0A84FF)
    val BlueLight = Color(0xFF409CFF)

    // Status
    val Green = Color(0xFF34C759)
    val GreenLight = Color(0xFF30D158)
    val Amber = Color(0xFFFF9500)
    val AmberLight = Color(0xFFFFD60A)
    val Red = Color(0xFFFF3B30)
    val RedLight = Color(0xFFFF453A)

    // Gray scale (shadcn zinc)
    val Gray50 = Color(0xFFFAFAFA)
    val Gray100 = Color(0xFFF4F4F5)
    val Gray200 = Color(0xFFE4E4E7)
    val Gray300 = Color(0xFFD4D4D8)
    val Gray400 = Color(0xFFA1A1AA)
    val Gray500 = Color(0xFF71717A)
    val Gray600 = Color(0xFF52525B)
    val Gray700 = Color(0xFF3F3F46)
    val Gray800 = Color(0xFF27272A)
    val Gray900 = Color(0xFF18181B)
    val Gray950 = Color(0xFF09090B)

    // Surface colors
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceDark = Color(0xFF0A0A0A)
    val CardLight = Color(0xFFF8F8F8)
    val CardDark = Color(0xFF141414)
    val BackgroundLight = Color(0xFFF5F5F5)
    val BackgroundDark = Color(0xFF09090B)
}

data class AppThemeColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val card: Color,
    val cardBorder: Color,
    val destructive: Color,
    val destructiveContainer: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val border: Color,
    val input: Color,
    val ring: Color,
    val isDark: Boolean
)

val LightColors = AppThemeColors(
    primary = AppColors.Blue,
    onPrimary = Color.White,
    primaryContainer = AppColors.Blue.copy(alpha = 0.1f),
    onPrimaryContainer = AppColors.Blue,
    secondary = AppColors.Gray100,
    onSecondary = AppColors.Gray900,
    secondaryContainer = AppColors.Gray200,
    onSecondaryContainer = AppColors.Gray800,
    background = AppColors.BackgroundLight,
    onBackground = AppColors.Gray900,
    surface = AppColors.SurfaceLight,
    onSurface = AppColors.Gray900,
    surfaceVariant = AppColors.Gray100,
    onSurfaceVariant = AppColors.Gray600,
    card = AppColors.CardLight,
    cardBorder = AppColors.Gray200,
    destructive = AppColors.Red,
    destructiveContainer = AppColors.Red.copy(alpha = 0.1f),
    success = AppColors.Green,
    successContainer = AppColors.Green.copy(alpha = 0.1f),
    warning = AppColors.Amber,
    warningContainer = AppColors.Amber.copy(alpha = 0.1f),
    muted = AppColors.Gray200,
    mutedForeground = AppColors.Gray500,
    accent = AppColors.Blue.copy(alpha = 0.1f),
    accentForeground = AppColors.Blue,
    border = AppColors.Gray200,
    input = AppColors.Gray200,
    ring = AppColors.Blue,
    isDark = false
)

val DarkColors = AppThemeColors(
    primary = AppColors.BlueDark,
    onPrimary = Color.Black,
    primaryContainer = AppColors.BlueDark.copy(alpha = 0.15f),
    onPrimaryContainer = AppColors.BlueLight,
    secondary = AppColors.Gray800,
    onSecondary = AppColors.Gray100,
    secondaryContainer = AppColors.Gray700,
    onSecondaryContainer = AppColors.Gray200,
    background = AppColors.BackgroundDark,
    onBackground = AppColors.Gray100,
    surface = AppColors.SurfaceDark,
    onSurface = AppColors.Gray100,
    surfaceVariant = AppColors.Gray800,
    onSurfaceVariant = AppColors.Gray400,
    card = AppColors.CardDark,
    cardBorder = AppColors.Gray800,
    destructive = AppColors.RedLight,
    destructiveContainer = AppColors.RedLight.copy(alpha = 0.15f),
    success = AppColors.GreenLight,
    successContainer = AppColors.GreenLight.copy(alpha = 0.15f),
    warning = AppColors.AmberLight,
    warningContainer = AppColors.AmberLight.copy(alpha = 0.15f),
    muted = AppColors.Gray800,
    mutedForeground = AppColors.Gray500,
    accent = AppColors.BlueDark.copy(alpha = 0.15f),
    accentForeground = AppColors.BlueLight,
    border = AppColors.Gray800,
    input = AppColors.Gray800,
    ring = AppColors.BlueDark,
    isDark = true
)

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
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = AppColors.BlueDark,
            onPrimary = Color.Black,
            secondary = AppColors.Gray800,
            onSecondary = AppColors.Gray100,
            background = AppColors.BackgroundDark,
            onBackground = AppColors.Gray100,
            surface = AppColors.SurfaceDark,
            onSurface = AppColors.Gray100,
            surfaceVariant = AppColors.Gray800,
            onSurfaceVariant = AppColors.Gray400,
            error = AppColors.RedLight,
            outline = AppColors.Gray700
        )
    } else {
        lightColorScheme(
            primary = AppColors.Blue,
            onPrimary = Color.White,
            secondary = AppColors.Gray100,
            onSecondary = AppColors.Gray900,
            background = AppColors.BackgroundLight,
            onBackground = AppColors.Gray900,
            surface = AppColors.SurfaceLight,
            onSurface = AppColors.Gray900,
            surfaceVariant = AppColors.Gray100,
            onSurfaceVariant = AppColors.Gray600,
            error = AppColors.Red,
            outline = AppColors.Gray200
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
