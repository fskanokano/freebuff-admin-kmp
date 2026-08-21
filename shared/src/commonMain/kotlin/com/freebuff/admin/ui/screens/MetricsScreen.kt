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

    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }
        return
    }
    val d = data!!

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            GroupSection(title = "Metrics", colors = colors) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Requests", style = MaterialTheme.typography.labelMedium, color = colors.mutedForeground)
                        Text("${d.requests_total}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Retries", style = MaterialTheme.typography.labelMedium, color = colors.mutedForeground)
                        Text("${d.transient_retries}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Models", style = MaterialTheme.typography.labelMedium, color = colors.mutedForeground)
                        Text("${d.models}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
                    }
                }
            }
        }

        item {
            GroupSection(title = "Trends", colors = colors) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Requests Trend", style = MaterialTheme.typography.bodySmall, color = colors.mutedForeground)
                        Text(d.requests_trend.direction.uppercase(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = when (d.requests_trend.direction) { "up" -> AppColors.Green; "down" -> AppColors.Red; else -> colors.mutedForeground })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Retries Trend", style = MaterialTheme.typography.bodySmall, color = colors.mutedForeground)
                        Text(d.retries_trend.direction.uppercase(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = when (d.retries_trend.direction) { "up" -> AppColors.Orange; "down" -> AppColors.Green; else -> colors.mutedForeground })
                    }
                }
            }
        }

        if (d.per_tokens.isNotEmpty()) {
            item {
                GroupSection(title = "Per Token", colors = colors) {
                    d.per_tokens.forEach { pt ->
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Token ${pt.token}", style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("req: ${pt.requests_24h} | retries: ${pt.transient_retries} | risk: ${pt.risk_level}", style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground)
                        }
                    }
                }
            }
        }
    }
}
