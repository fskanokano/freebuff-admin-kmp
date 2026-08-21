package com.freebuff.admin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Apple system colors
object AppColors {
    val Blue = Color(0xFF007AFF)
    val Green = Color(0xFF34C759)
    val Orange = Color(0xFFFF9500)
    val Red = Color(0xFFFF3B30)
    val Amber = Color(0xFFFFCC00)
    val Purple = Color(0xFFAF52DE)
    val Teal = Color(0xFF5AC8FA)

    // Gray scale (Apple exact values)
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

    // System backgrounds
    val SystemBg = Color(0xFFF2F2F7)
    val SecondaryBg = Color(0xFFE5E5EA)
    val CardBg = Color(0xFFFFFFFF)
    val GroupedBg = Color(0xFFF2F2F7)
}

// Apple design tokens
@Immutable
data class AppThemeColors(
    // Backgrounds
    val background: Color,
    val surface: Color,
    val card: Color,
    val groupedBackground: Color,
    // Text
    val label: Color,           // Primary text - full opacity
    val secondaryLabel: Color,  // Secondary text - 60% opacity
    val tertiaryLabel: Color,   // Tertiary text - 30% opacity
    val quaternaryLabel: Color, // Quaternary text - 10% opacity (separators)
    // System colors
    val primary: Color,
    val success: Color,
    val warning: Color,
    val destructive: Color,
    val purple: Color,
    val teal: Color,
    // Separators
    val separator: Color,
    val opaqueSeparator: Color,
    // Inputs
    val inputBackground: Color,
    val inputBorder: Color,
    // Fill
    val fill: Color,
    val secondaryFill: Color,
    val tertiaryFill: Color,
)

val LightColors = AppThemeColors(
    background = AppColors.SystemBg,
    surface = AppColors.CardBg,
    card = AppColors.CardBg,
    groupedBackground = AppColors.GroupedBg,
    label = AppColors.Gray900,
    secondaryLabel = AppColors.Gray500,
    tertiaryLabel = AppColors.Gray100,
    quaternaryLabel = AppColors.Gray400,
    primary = AppColors.Blue,
    success = AppColors.Green,
    warning = AppColors.Orange,
    destructive = AppColors.Red,
    purple = AppColors.Purple,
    teal = AppColors.Teal,
    separator = AppColors.Gray400,
    opaqueSeparator = AppColors.Gray300,
    inputBackground = AppColors.CardBg,
    inputBorder = AppColors.Gray300,
    fill = AppColors.Gray400,
    secondaryFill = AppColors.Gray200,
    tertiaryFill = AppColors.Gray100,
)

val LocalAppColors = staticCompositionLocalOf { LightColors }

object AppTheme {
    @Composable
    fun colors(): AppThemeColors = LocalAppColors.current
}

@Composable
fun FreebuffTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = AppColors.Blue,
        secondary = AppColors.Blue,
        background = AppColors.SystemBg,
        surface = AppColors.CardBg,
        onBackground = AppColors.Gray900,
        onSurface = AppColors.Gray900,
        surfaceVariant = AppColors.GroupedBg,
    )
    androidx.compose.runtime.CompositionLocalProvider(LocalAppColors provides LightColors) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
