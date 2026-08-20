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
fun LoginScreen(
    onLogin: (serverUrl: String, password: String) -> Unit,
    isLoading: Boolean,
    error: String? = null
) {
    val colors = AppTheme.colors()
    var serverUrl by remember { mutableStateOf("http://152.70.82.33:3457") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Title
            Text(
                text = "Freebuff Proxy",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = colors.onSurface
            )
            Text(
                text = "Admin Console",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.mutedForeground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Server URL
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Server URL", color = colors.mutedForeground) },
                placeholder = { Text("http://host:port", color = colors.mutedForeground) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colors.border,
                    focusedBorderColor = colors.primary,
                    unfocusedContainerColor = colors.inputBackground,
                    focusedContainerColor = colors.inputBackground,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface
                )
            )

            // Password / Admin Token
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Admin Token", color = colors.mutedForeground) },
                placeholder = { Text("Enter your admin token", color = colors.mutedForeground) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colors.border,
                    focusedBorderColor = colors.primary,
                    unfocusedContainerColor = colors.inputBackground,
                    focusedContainerColor = colors.inputBackground,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface
                )
            )

            // Error
            if (error != null) {
                Text(
                    text = error,
                    color = AppColors.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Login button
            Button(
                onClick = { onLogin(serverUrl, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && serverUrl.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.surface
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.surface,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Connect",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
