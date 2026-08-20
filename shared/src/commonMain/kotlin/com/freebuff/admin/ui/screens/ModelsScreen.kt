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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GroupSection(title = "Models (${d.models.size})", colors = colors) {
                d.models.forEachIndexed { idx, model ->
                    GroupRow(label = model, colors = colors)
                    if (idx < d.models.lastIndex) AppDivider()
                }
                if (d.models.isEmpty()) {
                    Text(
                        text = "No models configured",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        if (d.aliases.isNotEmpty()) {
            item {
                GroupSection(title = "Aliases (${d.aliases.size})", colors = colors) {
                    d.aliases.forEachIndexed { idx, (alias, target) ->
                        GroupRow(
                            label = alias,
                            colors = colors,
                            trailing = {
                                Text(
                                    text = "-> $target",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.mutedForeground
                                )
                            }
                        )
                        if (idx < d.aliases.size - 1) AppDivider()
                    }
                }
            }
        }

        if (d.agent_models.isNotEmpty()) {
            item {
                GroupSection(title = "Agent Models (${d.agent_models.size})", colors = colors) {
                    d.agent_models.forEachIndexed { idx, (agent, model) ->
                        GroupRow(
                            label = agent,
                            colors = colors,
                            trailing = {
                                Text(
                                    text = "-> $model",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.primary
                                )
                            }
                        )
                        if (idx < d.agent_models.size - 1) AppDivider()
                    }
                }
            }
        }
    }
}
