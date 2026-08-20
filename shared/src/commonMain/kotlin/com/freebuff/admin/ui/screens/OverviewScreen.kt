package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.api.*
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun OverviewScreen(viewModel: AppViewModel) {
    val data by viewModel.overviewData.collectAsState()
    val colors = AppTheme.colors()

    if (data == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
        // Status bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mode
                StatusBadge(
                    text = if (d.in_bridge) "Bridge (${d.bridge_tokens})" else d.mode,
                    color = if (d.in_bridge) AppColors.Purple else AppColors.Blue
                )
                // Version
                data?.let {
                    StatusBadge(text = "v${it.model_count}", color = colors.mutedForeground)
                }
                // Uptime
                if (d.uptime.isNotEmpty()) {
                    StatusBadge(text = "Up: ${d.uptime}", color = AppColors.Green)
                }
                if (d.safe_mode) {
                    StatusBadge(text = "Safe Mode", color = AppColors.Orange)
                }
            }
        }

        // Stats grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Tokens",
                    value = "${d.tokens.size}",
                    icon = Icons.Default.Key,
                    iconColor = AppColors.Blue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Models",
                    value = "${d.model_count}",
                    icon = Icons.Default.SmartToy,
                    iconColor = AppColors.Green,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Daily Limit",
                    value = "${d.max_messages_per_day}",
                    icon = Icons.Default.Speed,
                    iconColor = AppColors.Amber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Retries",
                    value = "${d.transient_retries}",
                    icon = Icons.Default.Refresh,
                    iconColor = AppColors.Orange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Token cards
        if (d.tokens.isNotEmpty()) {
            item {
                Text(
                    text = "Active Tokens",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurface
                )
            }

            items(d.tokens) { token ->
                AppCard(colors = colors) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DotIndicator(
                                color = when (token.session_status) {
                                    "live" -> AppColors.Green
                                    "standby" -> AppColors.Amber
                                    else -> AppColors.Gray50
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Token ${token.index + 1}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            StatusBadge(
                                text = token.session_status,
                                color = when (token.session_status) {
                                    "live" -> AppColors.Green
                                    "standby" -> AppColors.Amber
                                    else -> AppColors.Gray50
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Usage bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(colors.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(token.usage_pct / 100f)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            when {
                                                token.usage_pct >= 90 -> AppColors.Red
                                                token.usage_pct >= 70 -> AppColors.Orange
                                                else -> AppColors.Blue
                                            }
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${token.messages_24h}/${token.daily_limit}",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.mutedForeground
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Risk and queue
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (token.risk_level.isNotEmpty() && token.risk_level != "none") {
                                StatusBadge(
                                    text = token.risk_level,
                                    color = when (token.risk_level) {
                                        "high" -> AppColors.Red
                                        "medium" -> AppColors.Orange
                                        else -> AppColors.Amber
                                    }
                                )
                            }
                            if (token.queue_depth > 0) {
                                StatusBadge(
                                    text = "Queue: ${token.queue_depth}",
                                    color = AppColors.Blue
                                )
                            }
                            if (token.cooldown_active) {
                                StatusBadge(text = "Cooldown", color = AppColors.Orange)
                            }
                            if (token.has_standing) {
                                StatusBadge(
                                    text = token.standing_label,
                                    color = when (token.standing_level) {
                                        "vip" -> AppColors.Purple
                                        "trusted" -> AppColors.Green
                                        else -> AppColors.Amber
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
