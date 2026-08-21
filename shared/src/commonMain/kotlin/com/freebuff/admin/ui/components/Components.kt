package com.freebuff.admin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme
import com.freebuff.admin.ui.theme.AppThemeColors

// ── Inset Grouped Card (iOS Settings style) ──

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    colors: AppThemeColors = AppTheme.colors(),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.5.dp, colors.separator.copy(alpha = 0.3f)),
        content = content
    )
}

// ── Section Header (iOS Settings style) ──

@Composable
fun GroupSection(
    title: String,
    colors: AppThemeColors = AppTheme.colors(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                letterSpacing = 0.2.sp
            ),
            color = colors.secondaryLabel,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        AppCard(colors = colors) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

// ── Row (iOS Settings style) ──

@Composable
fun GroupRow(
    label: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    colors: AppThemeColors = AppTheme.colors()
) {
    val rowMod = if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    Row(
        modifier = rowMod.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.label,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

// ── Status Badge ──

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    colors: AppThemeColors = AppTheme.colors()
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = color
        )
    }
}

// ── Stat Card ──

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector? = null,
    iconColor: Color = AppColors.Blue,
    modifier: Modifier = Modifier,
    colors: AppThemeColors = AppTheme.colors()
) {
    AppCard(modifier = modifier, colors = colors) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = colors.label
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.secondaryLabel
            )
        }
    }
}

// ── Apple-style Button ──

@Composable
fun AppleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
    colors: AppThemeColors = AppTheme.colors()
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (destructive) colors.destructive else colors.primary,
            contentColor = Color.White,
            disabledContainerColor = colors.fill,
            disabledContentColor = colors.tertiaryLabel
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

// ── Pill Button ──

@Composable
fun PillButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: AppThemeColors = AppTheme.colors()
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) colors.primary else colors.fill)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (selected) Color.White else colors.label
        )
    }
}

// ── Divider ──

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    colors: AppThemeColors = AppTheme.colors()
) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = colors.separator
    )
}

// ── Dot Indicator ──

@Composable
fun DotIndicator(color: Color, size: Int = 8) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
    )
}
