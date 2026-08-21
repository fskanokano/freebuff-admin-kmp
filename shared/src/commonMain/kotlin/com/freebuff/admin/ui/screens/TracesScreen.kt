package com.freebuff.admin.ui.screens

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
fun TracesScreen(viewModel: AppViewModel) {
    val data by viewModel.traces.collectAsState()
    val colors = AppTheme.colors()
    if (data == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }; return }
    val d = data!!
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { GroupSection(title = "请求追踪", colors = colors) {} }
        items(d.traces) { t ->
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DotIndicator(color = when (t.status) { "ok" -> colors.success; "error" -> colors.destructive; else -> colors.tertiaryLabel })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t.model, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = colors.label, modifier = Modifier.weight(1f))
                        Text(t.ms, style = MaterialTheme.typography.bodySmall, color = colors.secondaryLabel)
                    }
                    if (t.token.isNotEmpty()) { Spacer(modifier = Modifier.height(4.dp)); Text("令牌: ${t.token}", style = MaterialTheme.typography.labelSmall, color = colors.secondaryLabel) }
                    if (t.error.isNotEmpty()) Text("错误: ${t.error}", style = MaterialTheme.typography.labelSmall, color = colors.destructive)
                    if (t.phases.isNotEmpty()) { Spacer(modifier = Modifier.height(2.dp)); t.phases.forEach { p -> Text("${p.key}: ${p.value}ms", style = MaterialTheme.typography.labelSmall, color = colors.secondaryLabel, modifier = Modifier.padding(start = 16.dp)) } }
                    Text(t.time, style = MaterialTheme.typography.labelSmall, color = colors.tertiaryLabel)
                }
            }
        }
    }
}
