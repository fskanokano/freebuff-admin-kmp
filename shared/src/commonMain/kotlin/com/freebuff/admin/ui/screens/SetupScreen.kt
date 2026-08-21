package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun SetupScreen(viewModel: AppViewModel) {
    val data by viewModel.setup.collectAsState()
    val colors = AppTheme.colors()
    if (data == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }; return }
    val d = data!!
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GroupSection(title = "服务器信息", colors = colors) {
            GroupRow(label = "地址", colors = colors, trailing = { Text(d.base_url, style = MaterialTheme.typography.bodySmall, color = colors.primary) })
            GroupRow(label = "模式", colors = colors, trailing = { StatusBadge(text = d.mode, color = if (d.bridge) colors.purple else colors.primary) })
            GroupRow(label = "API Key", colors = colors, trailing = { Text(d.key_hint, style = MaterialTheme.typography.bodySmall, color = colors.secondaryLabel) })
            GroupRow(label = "令牌数", colors = colors, trailing = { Text("${d.token_count}", style = MaterialTheme.typography.bodySmall, color = colors.label) })
        }
        GroupSection(title = "快速配置", colors = colors) {
            Surface(modifier = Modifier.fillMaxWidth().padding(12.dp), shape = RoundedCornerShape(8.dp), color = colors.groupedBackground) {
                Text("curl -X POST ${d.base_url}/chat/completions \\\n  -H \"Authorization: Bearer sk-any\" \\\n  -H \"Content-Type: application/json\" \\\n  -d '{\"model\":\"${d.model}\",\"messages\":[{\"role\":\"user\",\"content\":\"Hello\"}]}'", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, lineHeight = 16.sp), color = colors.label)
            }
        }
    }
}
