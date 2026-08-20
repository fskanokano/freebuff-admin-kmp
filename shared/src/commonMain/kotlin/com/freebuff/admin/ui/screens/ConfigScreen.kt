package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.model.ConfigData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun ConfigScreen(
    data: ConfigData,
    onSave: (ConfigData) -> Unit,
    onReload: () -> Unit,
    isSaving: Boolean
) {
    val colors = AppTheme.colors()
    var config by remember(data) { mutableStateOf(data) }
    var hasChanges by remember { mutableStateOf(false) }

    fun updateConfig(update: ConfigData.() -> ConfigData) {
        config = config.update()
        hasChanges = true
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Actions
        item {
            SectionHeader(
                title = "配置管理",
                actions = {
                    AppButton(
                        text = "重载配置",
                        onClick = onReload,
                        variant = ButtonVariant.Secondary
                    )
                    AppButton(
                        text = if (isSaving) "保存中..." else "💾 保存",
                        onClick = { onSave(config) },
                        enabled = hasChanges && !isSaving,
                        loading = isSaving
                    )
                }
            )
        }

        // Server
        item {
            ConfigSection(title = "🌐 服务器") {
                ConfigField(
                    label = "监听地址",
                    value = config.listen_addr,
                    onValueChange = { updateConfig { copy(listen_addr = it) } }
                )
                ConfigField(
                    label = "上游地址",
                    value = config.upstream_base_url,
                    onValueChange = { updateConfig { copy(upstream_base_url = it) } }
                )
            }
        }

        // Auth
        item {
            ConfigSection(title = "🔐 认证") {
                ConfigField(
                    label = "管理员令牌",
                    value = config.admin_token,
                    onValueChange = { updateConfig { copy(admin_token = it) } },
                    isPassword = true
                )
                ConfigField(
                    label = "API 密钥 (逗号分隔)",
                    value = config.api_keys.joinToString(","),
                    onValueChange = {
                        updateConfig { copy(api_keys = it.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }) }
                    }
                )
                ConfigToggle(
                    label = "安全模式",
                    checked = config.safe_mode,
                    description = "启用推荐的反封禁预设",
                    onCheckedChange = { updateConfig { copy(safe_mode = it) } }
                )
            }
        }

        // Routing
        item {
            ConfigSection(title = "🔀 路由") {
                ConfigField(
                    label = "轮换间隔",
                    value = config.rotation_interval,
                    onValueChange = { updateConfig { copy(rotation_interval = it) } },
                    placeholder = "例如: 30m"
                )
                ConfigField(
                    label = "请求超时",
                    value = config.request_timeout,
                    onValueChange = { updateConfig { copy(request_timeout = it) } },
                    placeholder = "例如: 120s"
                )
                ConfigField(
                    label = "最大每日消息",
                    value = config.max_messages_per_day.toString(),
                    onValueChange = { updateConfig { copy(max_messages_per_day = it.toIntOrNull() ?: 0) } },
                    placeholder = "0 = 无限制"
                )
                ConfigField(
                    label = "最大每日消费",
                    value = config.max_spend_per_day.toString(),
                    onValueChange = { updateConfig { copy(max_spend_per_day = it.toLongOrNull() ?: 0) } },
                    placeholder = "0 = 无限制"
                )
                ConfigToggle(
                    label = "隐藏不可用模型",
                    checked = config.models_hide_unavailable,
                    onCheckedChange = { updateConfig { copy(models_hide_unavailable = it) } }
                )
                ConfigToggle(
                    label = "HTTP/2 上游",
                    checked = config.http2_upstream,
                    onCheckedChange = { updateConfig { copy(http2_upstream = it) } }
                )
            }
        }

        // TLS & Security
        item {
            ConfigSection(title = "🔒 TLS & 安全") {
                ConfigField(
                    label = "TLS 指纹",
                    value = config.tls_fingerprint,
                    onValueChange = { updateConfig { copy(tls_fingerprint = it) } },
                    placeholder = "chrome120 / safari17 / random"
                )
                ConfigField(
                    label = "CORS 来源",
                    value = config.cors_allowed_origin,
                    onValueChange = { updateConfig { copy(cors_allowed_origin = it) } },
                    placeholder = "*"
                )
            }
        }

        // Session
        item {
            ConfigSection(title = "🔄 会话") {
                ConfigToggle(
                    label = "会话持久化",
                    checked = config.session_persist,
                    onCheckedChange = { updateConfig { copy(session_persist = it) } }
                )
                ConfigField(
                    label = "会话状态文件",
                    value = config.session_state_file,
                    onValueChange = { updateConfig { copy(session_state_file = it) } }
                )
                ConfigField(
                    label = "瞬态重试次数",
                    value = config.transient_retries.toString(),
                    onValueChange = { updateConfig { copy(transient_retries = it.toIntOrNull() ?: 1) } }
                )
            }
        }

        // Rate Limiting
        item {
            ConfigSection(title = "⚡ 限流") {
                ConfigField(
                    label = "每IP速率限制",
                    value = config.rate_limit_per_ip.toString(),
                    onValueChange = { updateConfig { copy(rate_limit_per_ip = it.toDoubleOrNull() ?: 0.0) } },
                    placeholder = "0 = 禁用"
                )
                ConfigField(
                    label = "突发容量",
                    value = config.rate_limit_burst.toString(),
                    onValueChange = { updateConfig { copy(rate_limit_burst = it.toIntOrNull() ?: 0) } }
                )
            }
        }

        // Dashboard
        item {
            ConfigSection(title = "🖥️ 仪表盘") {
                ConfigToggle(
                    label = "启用仪表盘",
                    checked = config.dashboard_enabled,
                    onCheckedChange = { updateConfig { copy(dashboard_enabled = it) } }
                )
                ConfigToggle(
                    label = "访问日志",
                    checked = config.log_access,
                    onCheckedChange = { updateConfig { copy(log_access = it) } }
                )
                ConfigField(
                    label = "日志级别",
                    value = config.log_level,
                    onValueChange = { updateConfig { copy(log_level = it) } },
                    placeholder = "debug / info / warn / error"
                )
                ConfigField(
                    label = "日志格式",
                    value = config.log_format,
                    onValueChange = { updateConfig { copy(log_format = it) } },
                    placeholder = "text / json"
                )
                ConfigField(
                    label = "日志环大小",
                    value = config.log_ring_size.toString(),
                    onValueChange = { updateConfig { copy(log_ring_size = it.toIntOrNull() ?: 500) } }
                )
            }
        }

        // Model Aliases
        item {
            ConfigSection(title = "🏷️ 模型别名") {
                if (config.model_aliases.isEmpty()) {
                    Text(
                        text = "暂无别名配置",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                } else {
                    config.model_aliases.forEach { (alias, real) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = alias,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                ),
                                color = colors.onSurface
                            )
                            Text(
                                text = "→ $real",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                ),
                                color = AppColors.Blue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = AppTheme.colors()
    AppCard(colors = colors) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ConfigField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false
) {
    val colors = AppTheme.colors()
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = colors.mutedForeground
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 13.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.border,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun ConfigToggle(
    label: String,
    checked: Boolean,
    description: String = "",
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = AppTheme.colors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colors.onSurface
            )
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = colors.mutedForeground,
                uncheckedTrackColor = colors.surfaceVariant
            )
        )
    }
}
