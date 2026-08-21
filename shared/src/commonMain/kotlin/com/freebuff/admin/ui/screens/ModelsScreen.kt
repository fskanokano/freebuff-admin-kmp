package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun ModelsScreen(viewModel: AppViewModel) {
    val data by viewModel.models.collectAsState()
    val colors = AppTheme.colors()
    if (data == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }; return }
    val d = data!!
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { GroupSection(title = "模型 (${d.count}) · ${d.agents} Agent", colors = colors) { d.models.forEachIndexed { idx, m -> GroupRow(label = m.id, colors = colors, trailing = { if (m.agent.isNotEmpty()) Text(m.agent, style = MaterialTheme.typography.bodySmall, color = colors.primary) }); if (idx < d.models.lastIndex) AppDivider() } } }
        if (d.has_aliases) { item { GroupSection(title = "别名 (${d.aliases.size})", colors = colors) { d.aliases.forEachIndexed { idx, a -> GroupRow(label = a.alias, colors = colors, trailing = { Text("→ ${a.real}", style = MaterialTheme.typography.bodySmall, color = colors.secondaryLabel) }); if (idx < d.aliases.lastIndex) AppDivider() } } } }
    }
}
