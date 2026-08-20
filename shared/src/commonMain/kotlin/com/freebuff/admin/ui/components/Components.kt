package com.freebuff.admin.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppThemeColors

// ── Card ──
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors(),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(20.dp),
        content = content
    )
}

// ── Stat Card ──
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: String? = null,
    color: Color = AppColors.Blue,
    modifier: Modifier = Modifier,
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()
) {
    AppCard(modifier = modifier, colors = colors) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    ),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ── Status Badge ──
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
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
}

// ── Section Header ──
@Composable
fun SectionHeader(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            color = colors.onSurface
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions()
        }
    }
}

// ── App Button ──
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()
) {
    val bgColor = when (variant) {
        ButtonVariant.Primary -> colors.primary
        ButtonVariant.Secondary -> colors.surface
        ButtonVariant.Destructive -> colors.destructive
        ButtonVariant.Ghost -> Color.Transparent
    }
    val contentColor = when (variant) {
        ButtonVariant.Primary -> colors.onPrimary
        ButtonVariant.Secondary -> colors.onSurface
        ButtonVariant.Destructive -> Color.White
        ButtonVariant.Ghost -> colors.primary
    }
    val borderColor = when (variant) {
        ButtonVariant.Secondary -> colors.border
        else -> Color.Transparent
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = contentColor,
            disabledContainerColor = bgColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        border = if (borderColor != Color.Transparent) ButtonDefaults.outlinedButtonBorder(enabled = enabled) else null,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp)
        )
    }
}

enum class ButtonVariant {
    Primary, Secondary, Destructive, Ghost
}

// ── Progress Bar ──
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = AppColors.Blue,
    trackColor: Color = AppColors.Gray200,
    height: Int = 6
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(height / 2))
                .background(color)
        )
    }
}

// ── Info Row ──
@Composable
fun InfoRow(
    label: String,
    value: String,
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.mutedForeground
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = colors.onSurface
        )
    }
}

// ── Divider ──
@Composable
fun AppDivider(colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = colors.border,
        thickness = 0.5.dp
    )
}

// ── Toast ──
@Composable
fun Toast(
    message: String,
    onDismiss: () -> Unit,
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()
) {
    AnimatedVisibility(
        visible = message.isNotEmpty(),
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = colors.card,
            shadowElevation = 8.dp,
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💬", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "✕",
                    modifier = Modifier.clickable(onClick = onDismiss),
                    color = colors.mutedForeground
                )
            }
        }
    }
}

// ── Empty State ──
@Composable
fun EmptyState(
    icon: String = "📭",
    title: String,
    description: String = "",
    colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurface
        )
        if (description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.mutedForeground
            )
        }
    }
}

// ── Loading ──
@Composable
fun LoadingOverlay(colors: AppThemeColors = com.freebuff.admin.ui.theme.AppTheme.colors()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = colors.primary,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.mutedForeground
            )
        }
    }
}

// ── Token Risk Color ──
fun riskColor(risk: String): Color = when (risk) {
    "high", "critical" -> AppColors.Red
    "moderate" -> AppColors.Amber
    else -> AppColors.Green
}

// ── Status Color ──
fun statusColor(status: String): Color = when {
    status.contains("ok", ignoreCase = true) || status.contains("active", ignoreCase = true) -> AppColors.Green
    status.contains("cooldown", ignoreCase = true) || status.contains("waiting", ignoreCase = true) -> AppColors.Amber
    status.contains("banned", ignoreCase = true) || status.contains("error", ignoreCase = true) -> AppColors.Red
    else -> AppColors.Gray400
}
