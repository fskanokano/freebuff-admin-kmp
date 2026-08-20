package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun LogsScreen(viewModel: AppViewModel) {
    val data by viewModel.logsData.collectAsState()
    val colors = AppTheme.colors()
    var searchQuery by remember { mutableStateOf("") }
    var expandedIdx by remember { mutableStateOf(-1) }

    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    val d = data!!

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        Surface(
            color = colors.surface,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search logs...", color = colors.mutedForeground) },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colors.inputBorder,
                        focusedBorderColor = colors.primary,
                        unfocusedContainerColor = colors.inputBackground,
                        focusedContainerColor = colors.inputBackground,
                        cursorColor = colors.primary,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface
                    )
                )
                GlassButton("Search", onClick = {
                    viewModel.setLogMsgFilter(searchQuery)
                    viewModel.searchLogs()
                })
            }
        }

        // Level filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val levels = listOf("" to "All", "info" to "INFO", "warn" to "WARN", "error" to "ERROR", "debug" to "DEBUG")
            levels.forEach { (level, label) ->
                val selected = d.level == level || (level.isEmpty() && d.level.isEmpty())
                PillButton(
                    text = label,
                    selected = selected,
                    onClick = { viewModel.setLogLevelFilter(level) }
                )
            }
        }

        // Log entries
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(d.entries.size) { idx ->
                val entry = d.entries[idx]
                val isExpanded = expandedIdx == idx

                AppCard(colors = colors) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StatusBadge(
                                text = entry.level,
                                color = when (entry.level) {
                                    "info" -> AppColors.Blue
                                    "warn" -> AppColors.Orange
                                    "error" -> AppColors.Red
                                    "debug" -> AppColors.Gray50
                                    else -> colors.mutedForeground
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = entry.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = entry.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.mutedForeground
                        )

                        // Expanded details
                        if (isExpanded && entry.fields.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.surfaceVariant
                            ) {
                                Text(
                                    text = entry.fields,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        lineHeight = 14.sp
                                    ),
                                    color = colors.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
