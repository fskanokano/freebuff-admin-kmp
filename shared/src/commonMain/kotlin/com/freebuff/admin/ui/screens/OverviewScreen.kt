package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Health status
        item {
            AppCard(colors = colors) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = if (d.healthy) "Healthy" else "Down",
                        color = if (d.healthy) AppColors.Green else AppColors.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = d.mode.uppercase(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AppColors.Blue
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "v${d.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }
                if (d.uptime.isNotEmpty()) {
                    Text(
                        text = "Uptime: ${d.uptime}",
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Total Requests",
                    value = "${d.total_requests}",
                    modifier = Modifier.weight(1f),
                    colors = colors
                )
                StatCard(
                    label = "Active Tokens",
                    value = "${d.active_tokens}/${d.total_tokens}",
                    modifier = Modifier.weight(1f),
                    colors = colors
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Avg Latency",
                    value = "${d.avg_latency_ms.toLong()}ms",
                    modifier = Modifier.weight(1f),
                    colors = colors
                )
                StatCard(
                    label = "Error Rate",
                    value = "${d.error_rate * 100}%",
                    modifier = Modifier.weight(1f),
                    colors = colors
                )
            }
        }

        // Token status
        if (d.token_status.isNotEmpty()) {
            item {
                GroupSection(title = "Token Pool", colors = colors) {
                    d.token_status.forEach { ts ->
                        GroupRow(
                            label = ts.key_hint,
                            colors = colors,
                            trailing = {
                                StatusBadge(
                                    text = ts.state,
                                    color = when (ts.state) {
                                        "active" -> AppColors.Green
                                        "idle" -> AppColors.Gray500
                                        "cooldown" -> AppColors.Orange
                                        "error" -> AppColors.Red
                                        else -> colors.mutedForeground
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

        // Recent routes
        if (d.recent_routes.isNotEmpty()) {
            item {
                GroupSection(title = "Recent Routes", colors = colors) {
                    d.recent_routes.take(5).forEach { route ->
                        GroupRow(
                            label = route.model,
                            colors = colors,
                            trailing = {
                                Text(
                                    text = "${route.latency_ms}ms ${route.status}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.mutedForeground
                                )
                            }
                        )
                    }
                }
            }
        }

        // Services
        if (d.services.isNotEmpty()) {
            item {
                GroupSection(title = "Services", colors = colors) {
                    d.services.forEach { svc ->
                        GroupRow(
                            label = svc.name,
                            colors = colors,
                            trailing = {
                                StatusBadge(
                                    text = if (svc.healthy) "OK" else "DOWN",
                                    color = if (svc.healthy) AppColors.Green else AppColors.Red
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
