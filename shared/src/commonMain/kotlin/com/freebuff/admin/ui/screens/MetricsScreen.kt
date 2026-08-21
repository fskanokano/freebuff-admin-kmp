package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun MetricsScreen(viewModel: AppViewModel) {
    val data by viewModel.metrics.collectAsState()
    val colors = AppTheme.colors()
    if (data == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }; return }
    val d = data!!
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            GroupSection(title = "概览", colors = colors) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) { Text("请求", style = MaterialTheme.typography.labelMedium, color = colors.secondaryLabel); Text("${d.requests_total}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = colors.label) }
                    Column(modifier = Modifier.weight(1f)) { Text("重试", style = MaterialTheme.typography.labelMedium, color = colors.secondaryLabel); Text("${d.transient_retries}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = colors.label) }
                    Column(modifier = Modifier.weight(1f)) { Text("模型", style = MaterialTheme.typography.labelMedium, color = colors.secondaryLabel); Text("${d.models}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = colors.label) }
                }
            }
        }
        item {
            GroupSection(title = "趋势", colors = colors) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) { Text("请求趋势", style = MaterialTheme.typography.bodySmall, color = colors.secondaryLabel); StatusBadge(text = d.requests_trend.direction.uppercase(), color = when (d.requests_trend.direction) { "up" -> colors.success; "down" -> colors.destructive; else -> colors.secondaryLabel }) }
                    Column(modifier = Modifier.weight(1f)) { Text("重试趋势", style = MaterialTheme.typography.bodySmall, color = colors.secondaryLabel); StatusBadge(text = d.retries_trend.direction.uppercase(), color = when (d.retries_trend.direction) { "up" -> colors.warning; "down" -> colors.success; else -> colors.secondaryLabel }) }
                }
            }
        }
        if (d.per_tokens.isNotEmpty()) {
            item { GroupSection(title = "各令牌", colors = colors) { d.per_tokens.forEach { pt -> Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Text("Token ${pt.token}", style = MaterialTheme.typography.bodyMedium, color = colors.label); Spacer(modifier = Modifier.weight(1f)); Text("请求: ${pt.requests_24h}  重试: ${pt.transient_retries}  风险: ${pt.risk_level}", style = MaterialTheme.typography.labelSmall, color = colors.secondaryLabel) } } } }
        }
    }
}
