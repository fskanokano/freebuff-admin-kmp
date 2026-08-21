package com.freebuff.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.theme.*

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, isLoading: Boolean, error: String? = null) {
    val colors = AppTheme.colors()
    var url by remember { mutableStateOf("http://152.70.82.33:3457") }
    var token by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            Text("Freebuff", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp), color = colors.label)
            Text("Proxy 管理后台", style = MaterialTheme.typography.bodyLarge, color = colors.secondaryLabel)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = url, onValueChange = { url = it }, modifier = Modifier.fillMaxWidth(), label = { Text("服务器地址") }, placeholder = { Text("http://host:port") }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = colors.inputBorder, focusedBorderColor = colors.primary, cursorColor = colors.primary, focusedTextColor = colors.label, unfocusedTextColor = colors.label, unfocusedContainerColor = colors.inputBackground, focusedContainerColor = colors.inputBackground))
            OutlinedTextField(value = token, onValueChange = { token = it }, modifier = Modifier.fillMaxWidth(), label = { Text("管理密码") }, placeholder = { Text("Admin Token") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = colors.inputBorder, focusedBorderColor = colors.primary, cursorColor = colors.primary, focusedTextColor = colors.label, unfocusedTextColor = colors.label, unfocusedContainerColor = colors.inputBackground, focusedContainerColor = colors.inputBackground))
            if (error != null) Text(error, color = colors.destructive, style = MaterialTheme.typography.bodySmall)
            Button(onClick = { onLogin(url, token) }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = !isLoading && url.isNotBlank() && token.isNotBlank(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.surface)) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = colors.surface, strokeWidth = 2.dp)
                else Text("连接", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}
