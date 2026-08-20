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
import com.freebuff.admin.model.TraceEntry
import com.freebuff.admin.model.TracesData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun TracesScreen(data: TracesData) {
    val colors = AppTheme.colors()

    if (!data.enabled) {
        EmptyState(
            icon = "🔍",
            title = "追踪未启用",
            description = "服务器未启用追踪日志"
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionHeader(title = "请求追踪 (${data.traces.size})")
        }

        if (data.traces.isEmpty()) {
            item {
                EmptyState(
                    icon = "📭",
                    title = "暂无追踪记录",
                    description = "等待请求到达后会自动显示"
                )
            }
        }

        items(data.traces) { trace ->
            TraceItem(trace, colors)
        }
    }
}

@Composable
private fun TraceItem(trace: TraceEntry, colors: AppThemeColors) {
    var expanded by remember { mutableStateOf(false) }

    AppCard(
        colors = colors,
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor(trace.status))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = trace.model.ifEmpty { "未知" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = colors.onSurface
                    )
                    if (trace.ms.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = trace.ms,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = colors.mutedForeground
                        )
                    }
                }
                Text(
                    text = "令牌: ${trace.token} · ${trace.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
            StatusBadge(
                text = trace.status,
                color = statusColor(trace.status)
            )
        }

        if (expanded && trace.phases.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            AppDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "阶段耗时",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.mutedForeground
            )
            Spacer(modifier = Modifier.height(6.dp))
            trace.phases.forEach { phase ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = phase.key,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = colors.mutedForeground
                    )
                    Text(
                        text = "${phase.value}ms",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        color = colors.onSurface
                    )
                }
            }
        }

        if (expanded && trace.error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "错误: ${trace.error}",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = colors.destructive
            )
        }
    }
}
