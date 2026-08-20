package com.freebuff.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.model.SetupData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun SetupScreen(data: SetupData) {
    val colors = AppTheme.colors()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Connection info
        item {
            AppCard(colors = colors) {
                SectionHeader(title = "🔗 连接信息")
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(label = "Base URL", value = data.base_url)
                InfoRow(label = "模式", value = data.mode)
                InfoRow(label = "默认模型", value = data.model)
                InfoRow(label = "令牌数", value = "${data.token_count}")
            }
        }

        // Client setup cards
        item {
            SectionHeader(title = "📋 客户端配置")
        }

        // OpenCode
        item {
            SetupCard(
                title = "OpenCode",
                icon = "💻",
                config = """
                    Base URL: ${data.base_url}
                    API Key: ${if (data.bridge) "your-freebuff-token" else "not-needed"}
                    Model: ${data.model}
                """.trimIndent(),
                colors = colors
            )
        }

        // Cursor / VS Code
        item {
            SetupCard(
                title = "Cursor / VS Code",
                icon = "📝",
                config = """
                    # settings.json
                    {
                      "openai.baseUrl": "${data.base_url}",
                      "openai.apiKey": "${if (data.bridge) "your-freebuff-token" else "not-needed"}",
                      "openai.model": "${data.model}"
                    }
                """.trimIndent(),
                colors = colors
            )
        }

        // Continue
        item {
            SetupCard(
                title = "Continue (VS Code)",
                icon = "🔄",
                config = """
                    # config.yaml
                    models:
                      - name: FreeBuff
                        provider: openai
                        model: ${data.model}
                        apiBase: ${data.base_url}
                        apiKey: "${if (data.bridge) "your-freebuff-token" else "not-needed"}"
                """.trimIndent(),
                colors = colors
            )
        }

        // Chatbox
        item {
            SetupCard(
                title = "Chatbox",
                icon = "💬",
                config = """
                    API Host: ${data.base_url}
                    API Key: ${if (data.bridge) "your-freebuff-token" else "not-needed"}
                    Model: ${data.model}
                """.trimIndent(),
                colors = colors
            )
        }

        // curl test
        item {
            SetupCard(
                title = "cURL 测试",
                icon = "🖥️",
                config = """
                    curl ${data.base_url}/chat/completions \
                      -H "Authorization: Bearer ${if (data.bridge) "your-freebuff-token" else "not-needed"}" \
                      -H "Content-Type: application/json" \
                      -d '{"model":"${data.model}","messages":[{"role":"user","content":"hi"}],"stream":true}
                """.trimIndent(),
                colors = colors
            )
        }

        // Models available
        item {
            AppCard(colors = colors) {
                SectionHeader(title = "🤖 可用模型")
                Spacer(modifier = Modifier.height(12.dp))
                data.models.forEach { model ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (model == data.model) AppColors.Green else AppColors.Gray400
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = model,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (model == data.model) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (model == data.model) AppColors.Green else colors.onSurface
                        )
                        if (model == data.model) {
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(text = "推荐", color = AppColors.Green)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupCard(
    title: String,
    icon: String,
    config: String,
    colors: AppThemeColors
) {
    AppCard(colors = colors) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onSurface
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = config,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp
            ),
            color = colors.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceVariant)
                .padding(12.dp)
        )
    }
}
