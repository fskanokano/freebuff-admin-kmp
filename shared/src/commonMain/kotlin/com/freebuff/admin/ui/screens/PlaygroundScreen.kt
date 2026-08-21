package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freebuff.admin.ui.AppViewModel
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PlaygroundScreen(viewModel: AppViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.chatLoading.collectAsState()
    val modelsData by viewModel.models.collectAsState()
    val availableModels = modelsData?.models?.map { it.id } ?: emptyList()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val colors = AppTheme.colors()
    var inputText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) listState.animateScrollToItem(chatMessages.lastIndex)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Model selector
        Surface(color = colors.surface, tonalElevation = 0.dp) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    PillButton(text = selectedModel.ifEmpty { "Select Model" }, selected = selectedModel.isNotEmpty(), onClick = { expanded = true })
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        availableModels.forEach { model ->
                            DropdownMenuItem(text = { Text(model) }, onClick = { viewModel.setSelectedModel(model); expanded = false })
                        }
                    }
                }
                if (chatMessages.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearChat() }) { Text("Clear", color = colors.mutedForeground) }
                }
            }
        }

        // Messages
        LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chatMessages) { (role, content) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (role == "user") Arrangement.End else Arrangement.Start) {
                    Surface(shape = RoundedCornerShape(12.dp), color = if (role == "user") colors.primary else colors.surfaceVariant, modifier = Modifier.widthIn(max = 300.dp)) {
                        Text(text = content, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
                    }
                }
            }
            if (isChatLoading) {
                item { Text("...", style = MaterialTheme.typography.bodyMedium, color = colors.mutedForeground) }
            }
        }

        // Input
        Surface(color = colors.surface, tonalElevation = 0.dp) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = inputText, onValueChange = { inputText = it }, modifier = Modifier.weight(1f), placeholder = { Text("Type a message...", color = colors.mutedForeground) }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = colors.border, focusedBorderColor = colors.primary, cursorColor = colors.primary, focusedTextColor = colors.onSurface, unfocusedTextColor = colors.onSurface))
                Button(onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendChat(inputText)
                        inputText = ""
                    }
                }, enabled = inputText.isNotBlank() && !isChatLoading, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.surface)) { Text("Send") }
            }
        }
    }
}
