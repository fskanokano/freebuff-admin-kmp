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
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*

@Composable
fun ConfigScreen(viewModel: AppViewModel) {
    val data by viewModel.config.collectAsState()
    val colors = AppTheme.colors()
    var editContent by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    if (data == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }; return }
    val d = data!!
    LaunchedEffect(d.env_content) { if (!isEditing) editContent = d.env_content }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppleButton("重载", onClick = { viewModel.reloadConfig() })
            if (isEditing) { AppleButton("取消", onClick = { isEditing = false; editContent = d.env_content }); AppleButton("保存", onClick = { viewModel.saveConfig(editContent); isEditing = false }) }
            else AppleButton("编辑", onClick = { isEditing = true })
        }
        Text(".env 配置", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal), color = colors.secondaryLabel, modifier = Modifier.padding(start = 16.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = colors.card, border = androidx.compose.foundation.BorderStroke(0.5.dp, colors.separator.copy(alpha = 0.3f))) {
            if (isEditing) OutlinedTextField(value = editContent, onValueChange = { editContent = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp).padding(12.dp), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = colors.inputBorder, focusedBorderColor = colors.primary, cursorColor = colors.primary, focusedTextColor = colors.label, unfocusedTextColor = colors.label), textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
            else Text(d.env_content.ifEmpty { "(空)" }, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, lineHeight = 18.sp), color = colors.label)
        }
        if (d.effective.isNotEmpty()) {
            Text("生效配置", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal), color = colors.secondaryLabel, modifier = Modifier.padding(start = 16.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = colors.card, border = androidx.compose.foundation.BorderStroke(0.5.dp, colors.separator.copy(alpha = 0.3f))) {
                Column { d.effective.entries.forEachIndexed { idx, e -> Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) { Text(e.key, style = MaterialTheme.typography.bodyMedium, color = colors.label, modifier = Modifier.weight(1f)); Text(e.value, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = colors.secondaryLabel) }; if (idx < d.effective.size - 1) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = colors.separator) } }
            }
        }
    }
}
