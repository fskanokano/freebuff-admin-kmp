package com.freebuff.admin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun LogsScreen(viewModel: AppViewModel) {
    val data by viewModel.logs.collectAsState()
    val colors = AppTheme.colors()
    var searchQuery by remember { mutableStateOf("") }
    var filterLevel by remember { mutableStateOf("") }
    var expandedId by remember { mutableStateOf(-1) }

    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    val d = data!!
    val filtered = d.entries.filter { entry ->
        val matchLevel = filterLevel.isEmpty() || entry.level == filterLevel
        val matchSearch = searchQuery.isEmpty() ||
            entry.msg.contains(searchQuery, ignoreCase = true) ||
            entry.request_id.contains(searchQuery, ignoreCase = true)
        matchLevel && matchSearch
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = colors.surface, tonalElevation = 0.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search...", color = colors.mutedForeground) },
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
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("" to "All", "info" to "INFO", "warn" to "WARN", "error" to "ERROR", "debug" to "DEBUG").forEach { (level, label) ->
                PillButton(
                    text = label,
                    selected = filterLevel == level,
                    onClick = { filterLevel = level }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filtered.size) { idx ->
                val entry = filtered[idx]
                val isExpanded = expandedId == idx

                AppCard(modifier = Modifier.clickable {
                    expandedId = if (isExpanded) -1 else idx
                }, colors = colors) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StatusBadge(
                                text = entry.level.uppercase(),
                                color = when (entry.level) {
                                    "info" -> AppColors.Blue
                                    "warn" -> AppColors.Orange
                                    "error" -> AppColors.Red
                                    "debug" -> AppColors.Gray500
                                    else -> colors.mutedForeground
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = entry.msg,
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
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "request_id: ${entry.request_id}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = colors.mutedForeground
                            )
                        }
                    }
                }
            }
        }
    }
}
