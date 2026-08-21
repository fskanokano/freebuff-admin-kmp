package com.freebuff.admin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun LogsScreen(viewModel: AppViewModel) {
    val data by viewModel.logs.collectAsState()
    val colors = AppTheme.colors()
    var filterLevel by remember { mutableStateOf("") }
    var expandedIdx by remember { mutableIntStateOf(-1) }

    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }
        return
    }
    val d = data!!

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("" to "ALL", "info" to "INFO", "warn" to "WARN", "error" to "ERROR", "debug" to "DEBUG").forEach { (level, label) ->
                PillButton(text = label, selected = filterLevel == level, onClick = { filterLevel = level })
            }
        }

        val entries = if (filterLevel.isEmpty()) d.entries else d.entries.filter { it.level == filterLevel }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(entries) { idx, entry ->
                val isExpanded = expandedIdx == idx
                Surface(modifier = Modifier.fillMaxWidth().clickable { expandedIdx = if (isExpanded) -1 else idx }, shape = RoundedCornerShape(8.dp), color = if (isExpanded) colors.surfaceVariant else colors.card) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(text = entry.level.uppercase(), color = when (entry.level) { "info" -> AppColors.Blue; "warn" -> AppColors.Orange; "error" -> AppColors.Red; "debug" -> AppColors.Gray500; else -> colors.mutedForeground })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(entry.message, style = MaterialTheme.typography.bodySmall, color = colors.onSurface, modifier = Modifier.weight(1f))
                        }
                        Text(entry.time, style = MaterialTheme.typography.labelSmall, color = colors.mutedForeground)
                        if (isExpanded && entry.fields.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(entry.fields, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = colors.onSurface)
                        }
                    }
                }
            }
        }
    }
}
