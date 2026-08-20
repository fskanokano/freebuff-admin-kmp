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

private fun fmt1(v: Double): String {
    val r = kotlin.math.round(v * 10) / 10
    return r.toString()
}

private fun fmt0(v: Double): String {
    return kotlin.math.round(v).toLong().toString()
}

@Composable
fun MetricsScreen(viewModel: AppViewModel) {
    val data by viewModel.metrics.collectAsState()
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
        item {
            GroupSection(title = "Overview", colors = colors) {
                MetricRow("Uptime", "${d.uptime_seconds / 3600}h ${(d.uptime_seconds % 3600) / 60}m")
                MetricRow("Total Requests", "${d.total_requests}")
                MetricRow("Requests/min", fmt1(d.requests_per_minute))
                MetricRow("Error Rate", fmt1(d.error_rate * 100) + "%")
                MetricRow("Avg Latency", fmt0(d.avg_latency_ms) + "ms")
                MetricRow("P50 Latency", fmt0(d.p50_latency_ms) + "ms")
                MetricRow("P90 Latency", fmt0(d.p90_latency_ms) + "ms")
                MetricRow("P99 Latency", fmt0(d.p99_latency_ms) + "ms")
            }
        }

        item {
            GroupSection(title = "Errors", colors = colors) {
                MetricRow("Total Retries", "${d.total_retries}")
                MetricRow("Retry Rate", fmt1(d.retry_rate * 100) + "%")
                MetricRow("Rate Limited", "${d.rate_limited_count}")
                MetricRow("Credit Exhausted", "${d.credit_exhausted_count}")
                MetricRow("TLS Errors", "${d.tls_error_count}")
                MetricRow("DNS Errors", "${d.dns_error_count}")
                MetricRow("Connection Errors", "${d.connection_error_count}")
            }
        }

        if (d.model_stats.isNotEmpty()) {
            item {
                GroupSection(title = "Models", colors = colors) {
                    d.model_stats.forEach { (model, stat) ->
                        GroupRow(
                            label = model,
                            colors = colors,
                            trailing = {
                                Text(
                                    "${stat.requests} req, ${fmt0(stat.avg_latency_ms)}ms avg",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.mutedForeground
                                )
                            }
                        )
                    }
                }
            }
        }

        if (d.token_stats.isNotEmpty()) {
            item {
                GroupSection(title = "Tokens", colors = colors) {
                    d.token_stats.forEach { stat ->
                        GroupRow(
                            label = stat.key_hint,
                            colors = colors,
                            trailing = {
                                StatusBadge(
                                    text = "${stat.requests} req / ${stat.in_flight} active",
                                    color = when (stat.state) {
                                        "active" -> AppColors.Green
                                        "cooldown" -> AppColors.Amber
                                        else -> AppColors.Gray500
                                    },
                                    colors = colors
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    val colors = AppTheme.colors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = colors.onSurface
        )
    }
}
