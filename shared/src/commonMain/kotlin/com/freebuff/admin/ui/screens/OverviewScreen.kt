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
        // Service health header
        item {
            GroupSection(title = "Service Health", colors = colors) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Mode:", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(
                        text = d.mode.uppercase(),
                        color = if (d.in_bridge) AppColors.Purple else AppColors.Blue
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Version:", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(d.uptime, style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
                }
                if (d.safe_mode) {
                    Row(modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)) {
                        StatusBadge(text = "Safe Mode ON", color = AppColors.Orange)
                    }
                }
            }
        }

        // Token pool
        item {
            GroupSection(title = "Token Pool", colors = colors) {
                if (d.tokens.isEmpty()) {
                    Text("No tokens", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall, color = colors.mutedForeground)
                }
            }
        }

        items(d.tokens) { token ->
            AppCard(modifier = Modifier.fillMaxWidth(), colors = colors) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Token ${token.index}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.onSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(
                            text = token.session_status.ifEmpty { "idle" },
                            color = when (token.session_status) {
                                "active" -> AppColors.Green
                                "cooldown" -> AppColors.Orange
                                "error" -> AppColors.Red
                                else -> colors.mutedForeground
                            }
                        )
                        if (token.cooldown_active) {
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(text = "Cooldown", color = AppColors.Orange)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (token.daily_limit > 0) {
                        LinearProgressIndicator(
                            progress = { token.usage_pct / 100f },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = when { token.usage_pct >= 90 -> AppColors.Red; token.usage_pct >= 60 -> AppColors.Orange; else -> AppColors.Green },
                            trackColor = colors.border
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        "${token.messages_24h} msgs/24h | ${token.requests} requests | ${token.active_runs} active | usage ${token.usage_pct}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // Stats
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(label = "Models", value = "${d.model_count}", modifier = Modifier.weight(1f), colors = colors)
                StatCard(label = "Max Msgs/Day", value = "${d.max_messages_per_day}", modifier = Modifier.weight(1f), colors = colors)
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(label = "Retries", value = "${d.transient_retries}", modifier = Modifier.weight(1f), colors = colors)
                StatCard(label = "Fingerprint Rotations", value = "${d.fingerprint_rotations}", modifier = Modifier.weight(1f), colors = colors)
            }
        }
    }
}
