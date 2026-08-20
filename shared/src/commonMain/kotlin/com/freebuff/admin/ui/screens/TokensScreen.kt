package com.freebuff.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.model.TokenDetail
import com.freebuff.admin.model.TokensData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun TokensScreen(
    data: TokensData,
    onTestToken: (Int) -> Unit,
    onTestAll: () -> Unit,
    onUnlockToken: (Int) -> Unit,
    onFinishToken: (Int) -> Unit,
    onAddToken: () -> Unit,
    onRemoveToken: (Int) -> Unit,
    onSwitchMode: (String) -> Unit
) {
    val colors = AppTheme.colors()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mode banner
        item {
            AppCard(colors = colors) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "当前模式",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                        Text(
                            text = if (data.in_bridge) "Bridge 模式" else "Pooled 模式",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.onSurface
                        )
                    }
                    StatusBadge(
                        text = data.mode,
                        color = if (data.in_bridge) AppColors.Amber else AppColors.Green
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppButton(
                        text = "切换到 Pooled",
                        onClick = { onSwitchMode("pooled") },
                        variant = if (!data.in_bridge) ButtonVariant.Primary else ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "切换到 Bridge",
                        onClick = { onSwitchMode("bridge") },
                        variant = if (data.in_bridge) ButtonVariant.Primary else ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Actions
        item {
            SectionHeader(
                title = "令牌管理 (${data.token_count})",
                actions = {
                    AppButton(
                        text = "全部测试",
                        onClick = onTestAll,
                        variant = ButtonVariant.Secondary
                    )
                    AppButton(
                        text = "+ 添加",
                        onClick = { showAddDialog = true },
                        variant = ButtonVariant.Primary
                    )
                }
            )
        }

        // Token cards
        if (data.tokens.isEmpty()) {
            item {
                EmptyState(
                    icon = "🔑",
                    title = "暂无令牌",
                    description = "点击上方「添加」按钮添加令牌"
                )
            }
        }

        itemsIndexed(data.tokens) { index, token ->
            TokenCard(
                token = token,
                index = index,
                onTest = { onTestToken(token.token) },
                onUnlock = { onUnlockToken(token.token) },
                onFinish = { onFinishToken(token.token) },
                onRemove = { onRemoveToken(token.token) },
                colors = colors
            )
        }
    }

    // Add token dialog
    if (showAddDialog) {
        AddTokenDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { token ->
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TokenCard(
    token: TokenDetail,
    index: Int,
    onTest: () -> Unit,
    onUnlock: () -> Unit,
    onFinish: () -> Unit,
    onRemove: () -> Unit,
    colors: AppThemeColors
) {
    var expanded by remember { mutableStateOf(false) }

    AppCard(
        colors = colors,
        onClick = { expanded = !expanded }
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Blue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${token.token}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.Blue
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "令牌 #${token.token}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurface
                    )
                    if (token.cooldown_active) {
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(text = "冷却中", color = AppColors.Amber)
                    }
                }
                Text(
                    text = "会话: ${token.session_status.ifEmpty { "无" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
            StatusBadge(
                text = token.risk_level.ifEmpty { "low" },
                color = riskColor(token.risk_level)
            )
        }

        // Stats row
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MiniStat("请求", "${token.requests}")
            MiniStat("24h", "${token.messages_24h}")
            MiniStat("重试", "${token.transient_retries}")
        }

        // Usage bar
        if (token.daily_limit > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "用量",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
                Text(
                    text = "${token.messages_24h}/${token.daily_limit} (${token.usage_pct}%)",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = colors.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            ProgressBar(
                progress = token.usage_pct / 100f,
                color = when {
                    token.usage_pct >= 90 -> AppColors.Red
                    token.usage_pct >= 70 -> AppColors.Amber
                    else -> AppColors.Green
                }
            )
        }

        // Expanded details
        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            AppDivider()

            // Quota details
            if (token.has_quota && token.quota.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "配额详情",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.mutedForeground
                )
                Spacer(modifier = Modifier.height(8.dp))
                token.quota.forEach { quota ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = quota.model,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurface
                        )
                        Text(
                            text = "${quota.recent}/${quota.limit} ${quota.resets_in}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                    }
                }
            }

            // Standing info
            if (token.has_standing) {
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "信誉等级", value = token.standing_label)
                InfoRow(label = "信誉分数", value = String.format("%.1f", token.standing_score))
            }

            // Actions
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppButton(
                    text = "测试",
                    onClick = onTest,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )
                if (token.cooldown_active) {
                    AppButton(
                        text = "解锁",
                        onClick = onUnlock,
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                AppButton(
                    text = "结束",
                    onClick = onFinish,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors().onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AppTheme.colors().mutedForeground
        )
    }
}

@Composable
private fun AddTokenDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var token by remember { mutableStateOf("") }
    val colors = AppTheme.colors()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加令牌") },
        text = {
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("FreeBuff Token") },
                placeholder = { Text("cb_...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.border
                )
            )
        },
        confirmButton = {
            AppButton(
                text = "添加",
                onClick = { onAdd(token) },
                enabled = token.isNotBlank()
            )
        },
        dismissButton = {
            AppButton(
                text = "取消",
                onClick = onDismiss,
                variant = ButtonVariant.Secondary
            )
        },
        containerColor = colors.card,
        titleContentColor = colors.onSurface,
        textContentColor = colors.onSurface
    )
}
