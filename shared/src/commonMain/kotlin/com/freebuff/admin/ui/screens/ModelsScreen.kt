package com.freebuff.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.freebuff.admin.model.ModelsData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun ModelsScreen(data: ModelsData) {
    val colors = AppTheme.colors()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary
        item {
            AppCard(colors = colors) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniStat("模型数", "${data.count}", AppColors.Blue)
                    MiniStat("代理数", "${data.agents}", AppColors.Green)
                    MiniStat("别名数", "${data.aliases.size}", AppColors.Amber)
                }
            }
        }

        // Models list
        item {
            SectionHeader(title = "可用模型")
        }

        if (data.models.isEmpty()) {
            item {
                EmptyState(icon = "🤖", title = "暂无模型")
            }
        }

        items(data.models) { model ->
            AppCard(colors = colors) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppColors.Blue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = model.id,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            color = colors.onSurface
                        )
                        if (model.agent.isNotEmpty()) {
                            Text(
                                text = "Agent: ${model.agent}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }
                    if (model.id.contains("flash")) {
                        StatusBadge(text = "推荐", color = AppColors.Green)
                    }
                }
            }
        }

        // Aliases
        if (data.has_aliases) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "模型别名")
            }

            items(data.aliases) { alias ->
                AppCard(colors = colors) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = alias.alias,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("→", color = colors.mutedForeground)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = alias.real,
                            style = MaterialTheme.typography.bodyMedium.copy(
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

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AppTheme.colors().mutedForeground
        )
    }
}
