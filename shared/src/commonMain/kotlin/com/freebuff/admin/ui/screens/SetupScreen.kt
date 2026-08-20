package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun SetupScreen(viewModel: AppViewModel) {
    val data by viewModel.setup.collectAsState()
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GroupSection(title = "Server Info", colors = colors) {
                GroupRow(
                    label = "Base URL",
                    colors = colors,
                    trailing = {
                        Text(
                            text = d.server_url.ifEmpty { "not set" },
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.primary
                        )
                    }
                )
            }
        }

        if (d.client_configs.isNotEmpty()) {
            item {
                GroupSection(title = "Client Configs", colors = colors) {
                    d.client_configs.forEach { (key, value) ->
                        GroupRow(
                            label = key,
                            colors = colors,
                            trailing = {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = colors.surfaceVariant
                                ) {
                                    Text(
                                        text = value,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 14.sp
                                        ),
                                        color = colors.onSurface
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // Quick setup examples
        item {
            GroupSection(title = "Quick Setup", colors = colors) {
                val baseUrl = d.server_url.ifEmpty { "http://YOUR_HOST:PORT" }
                val cUrl = """curl -X POST $baseUrl/v1/chat/completions \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"model":"deepseek/deepseek-v4-flash","messages":[{"role":"user","content":"Hello"}]}'"""
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant
                ) {
                    Text(
                        text = cUrl,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        ),
                        color = colors.onSurface
                    )
                }
            }
        }
    }
}
