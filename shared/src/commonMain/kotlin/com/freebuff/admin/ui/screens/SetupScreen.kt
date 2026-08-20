package com.freebuff.admin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun SetupScreen(viewModel: AppViewModel) {
    val data by viewModel.setupData.collectAsState()
    val colors = AppTheme.colors()
    val clipboardManager = LocalClipboardManager.current

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
        // Connection info
        item {
            GroupSection(title = "Connection", colors = colors) {
                GroupRow(label = "Base URL", colors = colors, trailing = {
                    Text(d.base_url, style = MaterialTheme.typography.bodySmall, color = colors.primary)
                })
                AppDivider()
                GroupRow(label = "Key", colors = colors, trailing = {
                    Text(d.key_hint, style = MaterialTheme.typography.bodySmall, color = colors.mutedForeground)
                })
                AppDivider()
                GroupRow(label = "Model", colors = colors, trailing = {
                    Text(d.model, style = MaterialTheme.typography.bodySmall, color = colors.onSurface)
                })
                AppDivider()
                GroupRow(label = "Mode", colors = colors, trailing = {
                    StatusBadge(
                        text = if (d.bridge) "Bridge" else d.mode,
                        color = if (d.bridge) AppColors.Purple else AppColors.Blue
                    )
                })
                AppDivider()
                GroupRow(label = "Tokens", colors = colors, trailing = {
                    Text("${d.token_count}", style = MaterialTheme.typography.bodySmall, color = colors.onSurface)
                })
            }
        }

        // OpenCode setup
        item {
            val baseUrl = d.base_url.ifEmpty { "http://YOUR_HOST:PORT" }
            val apiKey = d.key_hint.ifEmpty { "YOUR_API_KEY" }
            val setupCode = """export OPENAI_BASE_URL="$baseUrl/v1"
export OPENAI_API_KEY="$apiKey"

# Test
curl $baseUrl/v1/models -H "Authorization: Bearer $apiKey"
# Expected: {"object":"list","data":[{"id":"${d.model}","object":"model"}]}"""
            CodeBlock("OpenCode / VS Code", setupCode, clipboardManager, colors)
        }

        // Cursor
        item {
            val baseUrl = d.base_url.ifEmpty { "http://YOUR_HOST:PORT" }
            val apiKey = d.key_hint.ifEmpty { "YOUR_API_KEY" }
            val setupCode = """export ANTHROPIC_BASE_URL="$baseUrl"
export ANTHROPIC_API_KEY="$apiKey"
export ANTHROPIC_MODEL="${d.model}"
export OPENAI_BASE_URL="$baseUrl"
export OPENAI_API_KEY="$apiKey"

# OpenCode
export OPENCODE_PROVIDER=anthropic
export OPENCODE_MODEL="${d.model}"
export OPENCODE_MAX_TOKENS=8192"""
            CodeBlock("Cursor", setupCode, clipboardManager, colors)
        }

        // Continue
        item {
            val baseUrl = d.base_url.ifEmpty { "http://YOUR_HOST:PORT" }
            val apiKey = d.key_hint.ifEmpty { "YOUR_API_KEY" }
            val setupCode = """# Continue config (~/.continue/config.yaml)
models:
  - title: Freebuff
    provider: anthropic
    model: ${d.model}
    apiKey: $apiKey
    apiBase: $baseUrl"""
            CodeBlock("Continue", setupCode, clipboardManager, colors)
        }

        // Chatbox
        item {
            val baseUrl = d.base_url.ifEmpty { "http://YOUR_HOST:PORT" }
            val apiKey = d.key_hint.ifEmpty { "YOUR_API_KEY" }
            val setupCode = """API Host: Custom
Base URL: $baseUrl
API Key: $apiKey
Model: ${d.model}"""
            CodeBlock("Chatbox", setupCode, clipboardManager, colors)
        }

        // cURL
        item {
            val baseUrl = d.base_url.ifEmpty { "http://YOUR_HOST:PORT" }
            val apiKey = d.key_hint.ifEmpty { "YOUR_API_KEY" }
            val setupCode = """curl "$baseUrl/v1/chat/completions" \\
  -H "Content-Type: application/json" \\
  -H "Authorization: Bearer $apiKey" \\
  -d '{"model":"${d.model}","messages":[{"role":"user","content":"Hello"}]}'"""
            CodeBlock("cURL", setupCode, clipboardManager, colors)
        }
    }
}

@Composable
private fun CodeBlock(
    title: String,
    code: String,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    colors: AppThemeColors
) {
    AppCard(colors = colors) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                }) {
                    Text("Copy", fontSize = 12.sp, color = colors.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = colors.surfaceVariant
            ) {
                Text(
                    text = code,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        lineHeight = 16.sp
                    ),
                    color = colors.onSurface,
                    maxLines = 20,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
