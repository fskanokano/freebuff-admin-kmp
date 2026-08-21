package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
fun TokensScreen(viewModel: AppViewModel) {
    val data by viewModel.tokens.collectAsState()
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
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Mode: ", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                        StatusBadge(
                            text = d.mode.uppercase(),
                            color = if (d.in_bridge) AppColors.Purple else AppColors.Blue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("(${d.token_count} tokens)", style = MaterialTheme.typography.bodySmall, color = colors.mutedForeground)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GlassButton("Test All", onClick = { viewModel.testAllTokens() })
                        GlassButton("Add", onClick = { viewModel.addToken() })
                        GlassButton("Remove Last", onClick = { viewModel.removeToken() }, destructive = true)
                    }
                }
            }
        }

        itemsIndexed(d.tokens) { idx, token ->
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Token ${token.index}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.onSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(
                            text = token.session_status,
                            color = when (token.session_status) {
                                "active" -> AppColors.Green; "cooldown" -> AppColors.Orange; "error" -> AppColors.Red
                                else -> colors.mutedForeground
                            }
                        )
                        if (token.session_instance.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(token.session_instance, style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground)
                        }
                    }

                    if (token.daily_limit > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { token.usage_pct / 100f },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = when { token.usage_pct >= 90 -> AppColors.Red; token.usage_pct >= 60 -> AppColors.Orange; else -> AppColors.Green },
                            trackColor = colors.border
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("msgs/24h: ${token.messages_24h} | requests: ${token.requests} | active: ${token.active_runs} | risk: ${token.risk_level}", style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground)

                    // Quota per model
                    if (token.has_quota) {
                        Spacer(modifier = Modifier.height(8.dp))
                        token.quota.forEach { q ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                Text(q.model, style = MaterialTheme.typography.labelSmall, color = colors.onSurface, modifier = Modifier.weight(1f))
                                Text("${q.recent}/${q.limit} ${q.period}", style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground)
                                if (q.resets_in.isNotEmpty()) {
                                    Text(" (${q.resets_in})", style = MaterialTheme.typography.labelSmall, color = AppColors.Orange)
                                }
                            }
                            if (q.has_bar) {
                                LinearProgressIndicator(
                                    progress = { q.usage_pct / 100f },
                                    modifier = Modifier.fillMaxWidth().height(3.dp),
                                    color = if (q.near_limit) AppColors.Red else AppColors.Green,
                                    trackColor = colors.border
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.testToken(token.index) }, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("Test", style = MaterialTheme.typography.labelSmall) }
                        OutlinedButton(onClick = { viewModel.unlockToken(token.index) }, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("Unlock", style = MaterialTheme.typography.labelSmall) }
                        OutlinedButton(onClick = { viewModel.finishToken(token.index) }, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("Finish", style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        }
    }
}
