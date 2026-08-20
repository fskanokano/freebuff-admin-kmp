package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.api.*
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun TracesScreen(viewModel: AppViewModel) {
    val data by viewModel.tracesData.collectAsState()
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
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusBadge(
                    text = if (d.enabled) "Tracing ON" else "Tracing OFF",
                    color = if (d.enabled) AppColors.Green else AppColors.Gray50
                )
                StatusBadge(text = "${d.traces.size} traces", color = colors.mutedForeground)
            }
        }

        items(d.traces) { trace ->
            AppCard(colors = colors) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(
                            text = trace.status,
                            color = when {
                                trace.status.contains("ok") -> AppColors.Green
                                trace.status.contains("error") -> AppColors.Red
                                else -> AppColors.Amber
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
                            text = trace.ms,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                    }

                    if (trace.token.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Token: ${trace.token}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                    }

                    if (trace.error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = trace.error,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.Red
                        )
                    }

                    if (trace.phases.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        trace.phases.forEach { phase ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = phase.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.mutedForeground
                                )
                                Text(
                                    text = "${phase.ms}ms",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = trace.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }
    }
}
