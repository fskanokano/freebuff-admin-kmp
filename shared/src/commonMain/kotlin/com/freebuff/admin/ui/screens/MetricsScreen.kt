package com.freebuff.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.model.MetricsData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun MetricsScreen(data: MetricsData) {
    val colors = AppTheme.colors()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary
        item {
            SectionHeader(title = "实时指标")
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
                    label = "模型数",
                    value = "${data.models}",
                    icon = "🤖",
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
                    label = "重试次数",
                    value = "${data.transient_retries}",
                    icon = "🔄",
                    color = AppColors.Amber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "指纹轮换",
                    value = "${data.fingerprint_rotations}",
                    icon = "🔐",
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Trends
        item {
            AppCard(colors = colors) {
                SectionHeader(title = "趋势分析")
                Spacer(modifier = Modifier.height(12.dp))

                TrendRow(
                    label = "请求趋势",
                    trend = data.requests_trend.direction,
                    percentage = data.requests_trend.percentage,
                    colors = colors
                )
                Spacer(modifier = Modifier.height(8.dp))
                TrendRow(
                    label = "重试趋势",
                    trend = data.retries_trend.direction,
                    percentage = data.retries_trend.percentage,
                    colors = colors
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "采样次数: ${data.sample_count}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        // Per-token breakdown
        if (data.per_tokens.isNotEmpty()) {
            item {
                SectionHeader(title = "令牌明细")
            }

            items(data.per_tokens) { token ->
                AppCard(colors = colors) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppColors.Blue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${token.token}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AppColors.Blue
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "请求: ${token.requests_24h}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "消费: ${token.spend_day}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.mutedForeground
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "重试: ${token.transient_retries}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (token.transient_retries > 0) AppColors.Amber else colors.mutedForeground
                                )
                                Text(
                                    text = "轮换: ${token.fingerprint_rotations}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (token.fingerprint_rotations > 0) AppColors.Amber else colors.mutedForeground
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        StatusBadge(
                            text = token.risk_level.ifEmpty { "low" },
                            color = riskColor(token.risk_level)
                        )
                    }
                }
            }
        }

        // Prometheus info
        item {
            AppCard(colors = colors) {
                SectionHeader(title = "Prometheus 监控")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "指标数据以 Prometheus 格式暴露在 /metrics 端点",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "scrape_configs:\n  - job_name: \"freebuff-proxy\"\n    scrape_interval: 15s\n    static_configs:\n      - targets: [\"localhost:3457\"]",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = colors.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceVariant)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendRow(
    label: String,
    trend: String,
    percentage: Double,
    colors: AppThemeColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurface
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (trend) {
                "up" -> "↑"
                "down" -> "↓"
                else → "→"
            }
            val color = when (trend) {
                "up" -> AppColors.Green
                "down" -> AppColors.Red
                else -> colors.mutedForeground
            }
            Text(
                text = "$icon ${String.format("%.1f", percentage)}%",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = color
            )
        }
    }
}
