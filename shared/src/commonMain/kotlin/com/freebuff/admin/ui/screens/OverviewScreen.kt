package com.freebuff.admin.ui.screens
import com.freebuff.admin.ui.theme.AppThemeColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.model.OverviewData
import com.freebuff.admin.model.RouteEntry
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun OverviewScreen(data: OverviewData, onSmokeTest: () -> Unit) {
    val colors = AppTheme.colors()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Connection status
        item {
            val isHealthy = data.health == "ok" || data.health == "healthy"
            AppCard(colors = colors) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isHealthy) AppColors.Green else AppColors.Red)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "代理状态",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurface
                        )
                        Text(
                            text = if (isHealthy) "运行正常" else "连接异常",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                    }
                    StatusBadge(
                        text = data.mode.ifEmpty { "pooled" },
                        color = if (data.mode == "bridge") AppColors.Amber else AppColors.Green
                    )
                }
                if (data.uptime.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "运行时间", value = data.uptime)
                }
            }
        }

        // Statistics grid
        item {
            SectionHeader(title = "实时统计")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "总请求",
                    value = "${data.requests_total}",
                    icon = "📊",
                    color = AppColors.Blue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "今日请求",
                    value = "${data.requests_today}",
                    icon = "📈",
                    color = AppColors.Green,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "令牌数",
                    value = "${data.token_count}",
                    icon = "🔑",
                    color = AppColors.Amber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "模型数",
                    value = "${data.model_count}",
                    icon = "🤖",
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "今日消息",
                    value = "${data.messages_today}",
                    icon = "💬",
                    color = Color(0xFF06B6D4),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "今日消费",
                    value = data.spend_today.ifEmpty { "$0" },
                    icon = "💰",
                    color = AppColors.Green,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Token status overview
        item {
            SectionHeader(title = "令牌状态")
        }

        item {
            AppCard(colors = colors) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TokenStatusItem("活跃", data.tokens_active, AppColors.Green)
                    TokenStatusItem("空闲", data.tokens_idle, AppColors.Gray400)
                    TokenStatusItem("冷却", data.tokens_cooldown, AppColors.Amber)
                    TokenStatusItem("封禁", data.tokens_banned, AppColors.Red)
                }
            }
        }

        // Smoke test
        item {
            AppCard(colors = colors) {
                SectionHeader(title = "快速测试")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "发送一个简单的请求验证代理是否正常工作",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
                Spacer(modifier = Modifier.height(12.dp))
                AppButton(
                    text = "🚀 执行 Smoke Test",
                    onClick = onSmokeTest,
                    variant = ButtonVariant.Secondary
                )
            }
        }

        // Recent routes
        if (data.recent_routes.isNotEmpty()) {
            item {
                SectionHeader(title = "最近路由")
            }

            items(data.recent_routes.take(5)) { route ->
                RouteItem(route, colors)
            }
        }
    }
}

@Composable
private fun TokenStatusItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AppTheme.colors().mutedForeground
        )
    }
}

@Composable
private fun RouteItem(route: RouteEntry, colors: AppThemeColors) {
    AppCard(colors = colors) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (route.ok) AppColors.Green else AppColors.Red)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.name.ifEmpty { "未知" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.onSurface
                )
                Text(
                    text = "${route.model} · HTTP ${route.http} · ${route.ms}ms",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
            StatusBadge(
                text = if (route.ok) "成功" else "失败",
                color = if (route.ok) AppColors.Green else AppColors.Red
            )
        }
    }
}
