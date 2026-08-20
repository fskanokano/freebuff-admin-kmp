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
        // Mode and actions
        item {
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassButton("Test All", onClick = { viewModel.testAllTokens() })
                        GlassButton("Add", onClick = { viewModel.addToken() })
                        GlassButton("Remove Last", onClick = { viewModel.removeToken() }, destructive = true)
                    }
                }
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Total", "${d.total}", modifier = Modifier.weight(1f), colors = colors)
                StatCard("Active", "${d.active}", modifier = Modifier.weight(1f), iconColor = AppColors.Green, colors = colors)
                StatCard("In Flight", "${d.total_in_flight}/${d.max_in_flight}", modifier = Modifier.weight(1f), iconColor = AppColors.Orange, colors = colors)
            }
        }

        // Token list
        itemsIndexed(d.tokens) { idx, token ->
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(
                            text = token.state,
                            color = when (token.state) {
                                "active" -> AppColors.Green
                                "idle" -> AppColors.Gray500
                                "cooldown" -> AppColors.Orange
                                "error" -> AppColors.Red
                                else -> colors.mutedForeground
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = token.key_hint,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = colors.onSurface
                        )
                        if (token.pool_id.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = token.pool_id,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Usage bar
                    val total = token.requests + token.errors
                    val pct = if (total > 0) token.successes.toFloat() / total else 0f
                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = when {
                            pct >= 0.9f -> AppColors.Green
                            pct >= 0.5f -> AppColors.Orange
                            else -> AppColors.Red
                        },
                        trackColor = colors.border,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${token.requests} req / ${token.errors} err / ${token.successes} ok / ${token.in_flight} active",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.mutedForeground
                    )

                    // Actions
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.testToken(idx.toString()) },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) { Text("Test", style = MaterialTheme.typography.labelSmall) }

                        OutlinedButton(
                            onClick = { viewModel.unlockToken(idx.toString()) },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) { Text("Unlock", style = MaterialTheme.typography.labelSmall) }

                        OutlinedButton(
                            onClick = { viewModel.finishToken(idx.toString()) },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) { Text("Finish", style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        }
    }
}
