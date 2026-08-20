package com.freebuff.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.freebuff.admin.model.LogEntry
import com.freebuff.admin.model.LogsData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme

@Composable
fun LogsScreen(
    data: LogsData,
    filter: com.freebuff.admin.ui.LogFilter,
    onFilterChange: (com.freebuff.admin.ui.LogFilter) -> Unit
) {
    val colors = AppTheme.colors()
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    if (!data.enabled) {
        EmptyState(
            icon = "📝",
            title = "日志未启用",
            description = "服务器未启用内存日志环"
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter bar
        AppCard(
            colors = colors,
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Level filter
                FilterChip(
                    label = "全部",
                    selected = filter.level.isEmpty(),
                    onClick = { onFilterChange(filter.copy(level = "")) }
                )
                FilterChip(
                    label = "INFO",
                    selected = filter.level == "info",
                    color = AppColors.Green,
                    onClick = { onFilterChange(filter.copy(level = "info")) }
                )
                FilterChip(
                    label = "WARN",
                    selected = filter.level == "warn",
                    color = AppColors.Amber,
                    onClick = { onFilterChange(filter.copy(level = "warn")) }
                )
                FilterChip(
                    label = "ERROR",
                    selected = filter.level == "error",
                    color = AppColors.Red,
                    onClick = { onFilterChange(filter.copy(level = "error")) }
                )
                FilterChip(
                    label = "DEBUG",
                    selected = filter.level == "debug",
                    color = AppColors.Blue,
                    onClick = { onFilterChange(filter.copy(level = "debug")) }
                )
            }

            // Search
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = filter.message,
                onValueChange = { onFilterChange(filter.copy(message = it)) },
                placeholder = { Text("搜索日志消息...", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.border,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val entries = data.entries
            val errorCount = entries.count { it.level == "error" }
            val warnCount = entries.count { it.level == "warn" }
            val infoCount = entries.count { it.level == "info" }
            val debugCount = entries.count { it.level == "debug" }

            Text(
                text = "${entries.size} 条记录",
                style = MaterialTheme.typography.bodySmall,
                color = colors.mutedForeground
            )
            if (errorCount > 0) {
                Text(
                    text = "$errorCount 错误",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.Red
                )
            }
            if (warnCount > 0) {
                Text(
                    text = "$warnCount 警告",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.Amber
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Log entries
        if (data.entries.isEmpty()) {
            EmptyState(
                icon = "📭",
                title = "暂无日志",
                description = "等待日志记录生成..."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(data.entries) { index, entry ->
                    LogEntryItem(
                        entry = entry,
                        expanded = expandedIndex == index,
                        onClick = {
                            expandedIndex = if (expandedIndex == index) null else index
                        },
                        colors = colors
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    color: Color = AppColors.Blue,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) color.copy(alpha = 0.15f) else colors.surfaceVariant,
        modifier = Modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (selected) color else colors.mutedForeground,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LogEntryItem(
    entry: LogEntry,
    expanded: Boolean,
    onClick: () -> Unit,
    colors: AppThemeColors
) {
    AppCard(
        colors = colors,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(levelColor(entry.level))
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = entry.level.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = levelColor(entry.level)
                    )
                    Text(
                        text = entry.time,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = colors.mutedForeground
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface,
                    maxLines = if (expanded) Int.MAX_VALUE else 2
                )
                if (expanded && entry.fields.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.fields,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = colors.mutedForeground
                    )
                }
            }
        }
    }
}

private fun levelColor(level: String): Color = when (level.lowercase()) {
    "error" -> AppColors.Red
    "warn" -> AppColors.Amber
    "info" -> AppColors.Green
    "debug" -> AppColors.Blue
    else -> AppColors.Gray400
}
