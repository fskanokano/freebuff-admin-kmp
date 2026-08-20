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
import com.freebuff.admin.ui.components.AppCard
import com.freebuff.admin.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(viewModel: AppViewModel) {
    val metrics by viewModel.metrics.collectAsState()
    val colors = AppTheme.colors()

    LaunchedEffect(Unit) { viewModel.refresh() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Metrics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
        }

        metrics?.let { data ->
            item {
                AppCard(colors = colors) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Overview", style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                        Spacer(Modifier.height(8.dp))
                        MetricRow("Uptime", "${data.uptime_seconds / 3600}h ${(data.uptime_seconds % 3600) / 60}m")
                        MetricRow("Total Requests", "${data.total_requests}")
                        MetricRow("Requests/min", String.format("%.1f", data.requests_per_minute))
                        MetricRow("Error Rate", String.format("%.2f%%", data.error_rate * 100))
                        MetricRow("Avg Latency", "${String.format("%.0f", data.avg_latency_ms)}ms")
                        MetricRow("P50 Latency", "${String.format("%.0f", data.p50_latency_ms)}ms")
                        MetricRow("P90 Latency", "${String.format("%.0f", data.p90_latency_ms)}ms")
                        MetricRow("P99 Latency", "${String.format("%.0f", data.p99_latency_ms)}ms")
                    }
                }
            }

            item {
                AppCard(colors = colors) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Reliability", style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                        Spacer(Modifier.height(8.dp))
                        MetricRow("Total Retries", "${data.total_retries}")
                        MetricRow("Retry Rate", String.format("%.2f%%", data.retry_rate * 100))
                        MetricRow("Rate Limited", "${data.rate_limited_count}")
                        MetricRow("Credit Exhausted", "${data.credit_exhausted_count}")
                        MetricRow("TLS Errors", "${data.tls_error_count}")
                        MetricRow("DNS Errors", "${data.dns_error_count}")
                        MetricRow("Connection Errors", "${data.connection_error_count}")
                    }
                }
            }

            if (data.model_stats.isNotEmpty()) {
                item {
                    AppCard(colors = colors) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Model Stats", style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                            Spacer(Modifier.height(8.dp))
                            data.model_stats.forEach { (model, stat) ->
                                MetricRow(
                                    model,
                                    "${stat.requests} req, ${String.format("%.0f", stat.avg_latency_ms)}ms avg"
                                )
                            }
                        }
                    }
                }
            }

            if (data.token_stats.isNotEmpty()) {
                item {
                    AppCard(colors = colors) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Token Status", style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                            Spacer(Modifier.height(8.dp))
                            data.token_stats.forEach { token ->
                                MetricRow(
                                    token.key_hint,
                                    "${token.state} - ${token.requests} req, ${token.errors} err, ${token.in_flight} in-flight"
                                )
                            }
                        }
                    }
                }
            }

            if (data.routes.isNotEmpty()) {
                item {
                    AppCard(colors = colors) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Route Stats", style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                            Spacer(Modifier.height(8.dp))
                            data.routes.forEach { route ->
                                MetricRow(
                                    "${route.model} -> ${route.provider}",
                                    "${route.requests} req, ${route.errors} err, ${String.format("%.0f", route.avg_latency_ms)}ms"
                                )
                            }
                        }
                    }
                }
            }
        } ?: item {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.primary)
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    val colors = AppTheme.colors()
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = colors.onSurface, fontWeight = FontWeight.Medium)
    }
}
