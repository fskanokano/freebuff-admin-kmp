package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun TokensScreen(viewModel: AppViewModel) {
    val data by viewModel.tokensData.collectAsState()
    val colors = AppTheme.colors()
    var showAddDialog by remember { mutableStateOf(false) }
    var newToken by remember { mutableStateOf("") }

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
        // Mode switcher
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillButton("Pooled", d.mode == "pooled") { viewModel.switchMode("pooled") }
                if (!d.in_bridge) {
                    PillButton("Bridge", d.mode == "bridge") { viewModel.switchMode("bridge") }
                }
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(
                    text = "${d.token_count} token(s)",
                    color = colors.mutedForeground
                )
            }
        }

        // Actions
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassButton("Test All", onClick = { viewModel.testAllTokens() })
                Spacer(modifier = Modifier.weight(1f))
                GlassButton("Add", onClick = { showAddDialog = true })
                GlassButton("Remove Last", onClick = { viewModel.removeToken() }, destructive = true)
            }
        }

        // Token list
        items(d.tokens) { token ->
            TokenDetailCard(
                token = token,
                colors = colors,
                onTest = { viewModel.testToken(token.index) },
                onUnlock = { viewModel.unlockToken(token.index) },
                onFinish = { viewModel.finishToken(token.index) }
            )
        }
    }

    // Add token dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = colors.card,
            title = { Text("Add Token") },
            text = {
                OutlinedTextField(
                    value = newToken,
                    onValueChange = { newToken = it },
                    placeholder = { Text("Paste token value", color = colors.mutedForeground) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colors.inputBorder,
                        focusedBorderColor = colors.primary,
                        cursorColor = colors.primary,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addToken(newToken)
                        newToken = ""
                        showAddDialog = false
                    }
                ) {
                    Text("Add", color = colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = colors.mutedForeground)
                }
            }
        )
    }
}

@Composable
private fun TokenDetailCard(
    token: TokenDetail,
    colors: AppThemeColors,
    onTest: () -> Unit,
    onUnlock: () -> Unit,
    onFinish: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    AppCard(colors = colors) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                if (token.session_instance.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = token.session_instance,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.mutedForeground
                    )
                }
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

            // Status chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                if (token.cooldown_active) {
                    StatusBadge(text = "Cooldown", color = AppColors.Orange)
                }
                if (token.active_runs > 0) {
                    StatusBadge(text = "${token.active_runs} run(s)", color = AppColors.Blue)
                }
                if (token.transient_retries > 0) {
                    StatusBadge(text = "${token.transient_retries} retries", color = AppColors.Orange)
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

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassButton("Test", onClick = onTest)
                if (token.session_status == "standby" || token.cooldown_active) {
                    GlassButton("Unlock", onClick = onUnlock)
                }
                if (token.active_runs > 0) {
                    GlassButton("Finish", onClick = onFinish)
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        if (expanded) "Less" else "More",
                        color = colors.primary,
                        fontSize = 13.sp
                    )
                }
            }

            // Expanded details - quota
            if (expanded && token.has_quota) {
                AppDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quota Details",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                token.quota.forEach { q ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = q.model,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${q.recent}/${q.limit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                    }
                    if (q.has_bar) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(q.usage_pct / 100f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (q.near_limit) AppColors.Orange else AppColors.Blue
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
