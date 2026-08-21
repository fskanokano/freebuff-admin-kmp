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

    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }
    val d = data!!

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            GroupSection(title = "Models (${d.count}) - ${d.agents} Agents", colors = colors) {
                d.models.forEachIndexed { idx, model ->
                    GroupRow(label = model.id, colors = colors, trailing = {
                        if (model.agent.isNotEmpty()) Text(model.agent, style = MaterialTheme.typography.bodySmall, color = colors.primary)
                    })
                    if (idx < d.models.lastIndex) AppDivider()
                }
            }
        }
        if (d.has_aliases) {
            item {
                GroupSection(title = "Aliases (${d.aliases.size})", colors = colors) {
                    d.aliases.forEachIndexed { idx, alias ->
                        GroupRow(label = alias.alias, colors = colors, trailing = {
                            Text("-> ${alias.real}", style = MaterialTheme.typography.bodySmall, color = colors.mutedForeground)
                        })
                        if (idx < d.aliases.lastIndex) AppDivider()
                    }
                }
            }
        }
    }
}
