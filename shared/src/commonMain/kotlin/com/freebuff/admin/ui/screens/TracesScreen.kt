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

    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }
    val d = data!!

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            GroupSection(title = "Traces", colors = colors) {}
        }
        items(d.traces) { trace ->
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(text = trace.status, color = when (trace.status) { "ok" -> AppColors.Green; "error" -> AppColors.Red; else -> colors.mutedForeground })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(trace.model, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = colors.onSurface)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(trace.ms, style = MaterialTheme.typography.bodySmall, color = colors.mutedForeground)
                    }
                    if (trace.token.isNotEmpty()) Text("Token: ${trace.token}", style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground)
                    if (trace.error.isNotEmpty()) Text("Error: ${trace.error}", style = MaterialTheme.typography.labelSmall, color = AppColors.Red)
                    if (trace.phases.isNotEmpty()) {
                        trace.phases.forEach { phase ->
                            Text("${phase.key}: ${phase.value}ms", style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    Text(trace.time, style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground)
                }
            }
        }
    }
}
