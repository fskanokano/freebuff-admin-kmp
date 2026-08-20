package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
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
fun ConfigScreen(viewModel: AppViewModel) {
    val data by viewModel.configData.collectAsState()
    val colors = AppTheme.colors()
    var editContent by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    val d = data!!

    // Sync content when data loads
    LaunchedEffect(d.env_content) {
        if (!isEditing) {
            editContent = d.env_content
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassButton("Reload", onClick = { viewModel.reloadConfig() })
            Spacer(modifier = Modifier.weight(1f))
            if (isEditing) {
                GlassButton("Cancel", onClick = {
                    isEditing = false
                    editContent = d.env_content
                })
                GlassButton("Save", onClick = {
                    viewModel.saveConfig(editContent)
                    isEditing = false
                })
            } else {
                GlassButton("Edit", onClick = { isEditing = true })
            }
        }

        // .env content editor
        GroupSection(title = ".env Configuration", colors = colors) {
            if (isEditing) {
                OutlinedTextField(
                    value = editContent,
                    onValueChange = { editContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 300.dp)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colors.inputBorder,
                        focusedBorderColor = colors.primary,
                        cursorColor = colors.primary,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface
                    ),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant
                ) {
                    Text(
                        text = d.env_content.ifEmpty { "(no .env file)" },
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            lineHeight = 18.sp
                        ),
                        color = colors.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Effective config
        if (d.effective.isNotEmpty()) {
            GroupSection(title = "Effective Configuration", colors = colors) {
                d.effective.forEachIndexed { idx, kv ->
                    GroupRow(
                        label = kv.key,
                        colors = colors,
                        trailing = {
                            Text(
                                text = if (kv.secret) "***" else kv.value,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    )
                    if (idx < d.effective.lastIndex) AppDivider()
                }
            }
        }
    }
}
