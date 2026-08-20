package com.freebuff.admin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

// -- AppCard: insetGrouped iOS style --

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
        border = androidx.compose.foundation.BorderStroke(0.5.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content
    )
}

// -- GroupSection: section header --

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
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            ),
            color = colors.mutedForeground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        AppCard(colors = colors) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

// -- GroupRow: setting row --

@Composable
fun GroupRow(
    label: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    colors: AppThemeColors = AppTheme.colors()
) {
    val rowMod = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Row(
        modifier = rowMod
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

// -- StatusBadge --

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

// -- StatCard --

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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.mutedForeground
            )
        }
    }
}

// -- GlassButton --

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
    colors: AppThemeColors = AppTheme.colors()
) {
    val bgColor = when {
        !enabled -> colors.border
        destructive -> AppColors.Red
        else -> colors.primary
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = Color.White,
            disabledContainerColor = colors.border,
            disabledContentColor = colors.mutedForeground
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

// -- PillButton (for mode switcher) --

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
            .background(if (selected) colors.primary else colors.border)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (selected) Color.White else colors.onSurface
        )
    }
}

// -- Divider --

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    colors: AppThemeColors = AppTheme.colors()
) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = colors.border
    )
}

// -- Dot indicator --

@Composable
fun DotIndicator(
    color: Color,
    size: Int = 8
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
    )
}

// -- Toast-like message --

@Composable
fun ToastSnackbar(
    message: String,
    onDismiss: () -> Unit,
    colors: AppThemeColors = AppTheme.colors()
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        containerColor = colors.card,
        contentColor = colors.onSurface,
        shape = RoundedCornerShape(12.dp),
        action = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = colors.primary)
            }
        }
    ) {
        Text(message)
    }
}
