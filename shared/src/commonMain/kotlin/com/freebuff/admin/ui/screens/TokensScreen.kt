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
    if (data == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }; return }
    val d = data!!

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("令牌管理", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = colors.label)
                        Spacer(modifier = Modifier.weight(1f))
                        StatusBadge(text = "${d.token_count} 个", color = colors.secondaryLabel)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppleButton("全部测试", onClick = { viewModel.testAllTokens() })
                        AppleButton("添加", onClick = { viewModel.addToken() })
                        AppleButton("删除末尾", onClick = { viewModel.removeToken() }, destructive = true)
                    }
                }
            }
        }

        itemsIndexed(d.tokens) { idx, token ->
            AppCard(colors = colors) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DotIndicator(color = when (token.session_status) { "active" -> colors.success; "cooldown" -> colors.warning; "error" -> colors.destructive; else -> colors.tertiaryLabel })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Token ${token.index}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.label)
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(text = token.session_status, color = when (token.session_status) { "active" -> colors.success; "cooldown" -> colors.warning; "error" -> colors.destructive; else -> colors.secondaryLabel })
                    }
                    if (token.daily_limit > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { token.usage_pct / 100f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = when { token.usage_pct >= 90 -> colors.destructive; token.usage_pct >= 60 -> colors.warning; else -> colors.success }, trackColor = colors.fill)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("24h: ${token.messages_24h}  请求: ${token.requests}  活跃: ${token.active_runs}", style = MaterialTheme.typography.labelSmall, color = colors.secondaryLabel)
                    if (token.has_quota) {
                        Spacer(modifier = Modifier.height(6.dp))
                        token.quota.forEach { q ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(q.model, style = MaterialTheme.typography.labelSmall, color = colors.label, modifier = Modifier.weight(1f))
                                Text("${q.recent}/${q.limit}", style = MaterialTheme.typography.labelSmall, color = colors.secondaryLabel)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.testToken(token.index) }, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)) { Text("测试", style = MaterialTheme.typography.labelSmall) }
                        OutlinedButton(onClick = { viewModel.unlockToken(token.index) }, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)) { Text("解锁", style = MaterialTheme.typography.labelSmall) }
                        OutlinedButton(onClick = { viewModel.finishToken(token.index) }, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)) { Text("结束", style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        }
    }
}
