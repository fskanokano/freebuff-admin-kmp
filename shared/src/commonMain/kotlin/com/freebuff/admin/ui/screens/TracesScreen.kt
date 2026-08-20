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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GroupSection(title = "Traces (${d.traces.size})", colors = colors) {
                // empty header
            }
        }

        items(d.traces) { trace ->
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(
                            text = trace.status,
                            color = when {
                                trace.status.contains("ok", ignoreCase = true) -> AppColors.Green
                                trace.status.contains("error", ignoreCase = true) -> AppColors.Red
                                else -> colors.mutedForeground
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = trace.model,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${trace.latency_ms}ms",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                    }

                    if (trace.provider.isNotEmpty()) {
                        Text(
                            text = "Provider: ${trace.provider}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.mutedForeground
                        )
                    }

                    if (trace.stages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Stages:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.mutedForeground
                        )
                        trace.stages.forEach { stage ->
                            Row(
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${stage.name}: ${stage.status}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${stage.latency_ms}ms",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.mutedForeground
                                )
                            }
                        }
                    }

                    Text(
                        text = trace.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }
    }
}
