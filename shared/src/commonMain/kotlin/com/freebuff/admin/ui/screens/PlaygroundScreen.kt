package com.freebuff.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.model.SetupData
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun PlaygroundScreen(
    setupData: SetupData,
    onSend: (model: String, prompt: String) -> Unit,
    isLoading: Boolean
) {
    val colors = AppTheme.colors()
    var selectedModel by remember { mutableStateOf(setupData.model) }
    var prompt by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        AppCard(
            colors = colors,
            modifier = Modifier.padding(20.dp)
        ) {
            SectionHeader(title = "💬 聊天测试")
            Spacer(modifier = Modifier.height(12.dp))

            // Model selector
            Text(
                text = "选择模型",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = colors.mutedForeground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                setupData.models.take(4).forEach { model ->
                    Surface(
                        onClick = { selectedModel = model },
                        shape = RoundedCornerShape(8.dp),
                        color = if (model == selectedModel) AppColors.Blue.copy(alpha = 0.15f) else colors.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = model.split("/").last().take(12),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (model == selectedModel) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (model == selectedModel) AppColors.Blue else colors.mutedForeground,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Chat messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg, colors)
            }
        }

        // Input
        AppCard(
            colors = colors,
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("输入测试消息...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 1,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                AppButton(
                    text = if (isLoading) "..." else "发送",
                    onClick = {
                        if (prompt.isNotBlank()) {
                            messages = messages + ChatMessage(prompt, true)
                            onSend(selectedModel, prompt)
                            prompt = ""
                            scope.launch {
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    },
                    enabled = prompt.isNotBlank() && !isLoading,
                    loading = isLoading
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, colors: AppThemeColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.Blue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🤖", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) AppColors.Blue else colors.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isUser) Color.White else colors.onSurface,
                modifier = Modifier.padding(12.dp)
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.Green.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 14.sp)
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)
