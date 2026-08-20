package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
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
import com.freebuff.admin.ui.theme.*

@Composable
fun ConfigScreen(viewModel: AppViewModel) {
    val data by viewModel.config.collectAsState()
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

    LaunchedEffect(d.env_content) {
        if (!isEditing) {
            editContent = d.env_content
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = { viewModel.reloadConfig() }) {
                Text("Reload")
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isEditing) {
                OutlinedButton(onClick = {
                    isEditing = false
                    editContent = d.env_content
                }) { Text("Cancel") }
                Button(onClick = {
                    viewModel.saveConfig(editContent)
                    isEditing = false
                }) { Text("Save") }
            } else {
                Button(onClick = { isEditing = true }) { Text("Edit") }
            }
        }

        Text(
            text = ".env Configuration",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            color = colors.card
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = editContent,
                    onValueChange = { editContent = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp).padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colors.border,
                        focusedBorderColor = colors.primary,
                        cursorColor = colors.primary,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface
                    ),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )
            } else {
                Text(
                    text = d.env_content.ifEmpty { "(empty)" },
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    ),
                    color = colors.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (d.effective.isNotEmpty()) {
            Text(
                text = "Effective Config",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors.card
            ) {
                Column {
                    d.effective.entries.forEachIndexed { idx, (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = colors.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = colors.mutedForeground
                            )
                        }
                        if (idx < d.effective.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = colors.border
                            )
                        }
                    }
                }
            }
        }
    }
}
