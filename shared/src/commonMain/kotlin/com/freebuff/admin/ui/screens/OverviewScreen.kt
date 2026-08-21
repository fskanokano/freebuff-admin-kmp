package com.freebuff.admin.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun OverviewScreen(viewModel: AppViewModel) {
    val data by viewModel.overview.collectAsState()
    val colors = AppTheme.colors()
    if (data == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }; return }
    val d = data!!

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DotIndicator(color = if (d.mode == "bridge") colors.purple else colors.success, size = 10)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("服务状态", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = colors.label)
                        Spacer(modifier = Modifier.weight(1f))
                        StatusBadge(text = if (d.in_bridge) "Bridge" else "Pooled", color = if (d.in_bridge) colors.purple else colors.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("运行 ${d.uptime}", style = MaterialTheme.typography.bodySmall, color = colors.secondaryLabel)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(label = "模型", value = "${d.model_count}", modifier = Modifier.weight(1f), colors = colors)
                StatCard(label = "每日上限", value = "${d.max_messages_per_day}", modifier = Modifier.weight(1f), colors = colors)
            }
        }

        if (d.tokens.isNotEmpty()) {
            item { Text("令牌池", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal), color = colors.secondaryLabel, modifier = Modifier.padding(start = 16.dp, top = 8.dp)) }
            items(d.tokens.size) { idx ->
                val token = d.tokens[idx]
                AnimatedVisibility(visible = true, enter = fadeIn(tween(200, delayMillis = idx * 50)) + slideInVertically(tween(200, delayMillis = idx * 50)) { it / 2 }) {
                AppCard(colors = colors) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DotIndicator(color = when (token.session_status) { "active" -> colors.success; "cooldown" -> colors.warning; "error" -> colors.destructive; else -> colors.tertiaryLabel })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Token ${token.index}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.label)
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(text = token.session_status.ifEmpty { "空闲" }, color = when (token.session_status) { "active" -> colors.success; "cooldown" -> colors.warning; "error" -> colors.destructive; else -> colors.secondaryLabel })
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (token.daily_limit > 0) {
                            LinearProgressIndicator(progress = { token.usage_pct / 100f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = when { token.usage_pct >= 90 -> colors.destructive; token.usage_pct >= 60 -> colors.warning; else -> colors.success }, trackColor = colors.fill)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text("24h: ${token.messages_24h}  活跃: ${token.active_runs}  使用率: ${token.usage_pct}%", style = MaterialTheme.typography.labelSmall, color = colors.secondaryLabel)
                    }
                }
                }
            }
        }
    }
}
