package com.freebuff.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.screens.*
import com.freebuff.admin.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun App(viewModel: AppViewModel) {
    FreebuffTheme {
        val connectionState by viewModel.api.connectionState.collectAsState()
        val currentScreen by viewModel.currentScreen.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val toastMessage by viewModel.toastMessage.collectAsState()
        val scope = rememberCoroutineScope()

        var loginError by remember { mutableStateOf<String?>(null) }
        var initialized by remember { mutableStateOf(false) }

        // Try restore session on first load
        LaunchedEffect(Unit) {
            if (!initialized) {
                initialized = true
                viewModel.tryRestoreSession()
            }
        }

        when (connectionState) {
            is ConnectionState.Disconnected -> {
                LoginScreen(
                    onLogin = { serverUrl, password ->
                        loginError = null
                        scope.launch {
                            val success = viewModel.login(serverUrl, password)
                            if (!success) loginError = "Connection failed"
                        }
                    },
                    isLoading = isLoading,
                    error = loginError
                )
            }
            is ConnectionState.Connected -> {
                MainContent(viewModel = viewModel, currentScreen = currentScreen, isLoading = isLoading)
            }
        }

        toastMessage?.let { msg ->
            Snackbar(modifier = Modifier.padding(16.dp), containerColor = AppColors.Gray900, contentColor = AppColors.Surface, shape = RoundedCornerShape(12.dp)) { Text(msg) }
        }
    }
}

@Composable
private fun MainContent(viewModel: AppViewModel, currentScreen: Screen, isLoading: Boolean) {
    val colors = AppTheme.colors()

    Scaffold(
        containerColor = colors.background,
        topBar = {
            Surface(color = colors.surface, tonalElevation = 0.dp) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Freebuff Proxy", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp), color = colors.onSurface)
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.logout() }) { Text("Exit", color = colors.onSurface) }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = colors.surface, tonalElevation = 0.dp) {
                listOf(
                    ScreenData(Screen.Overview, "Home", Icons.Default.Home),
                    ScreenData(Screen.Tokens, "Tokens", Icons.Default.List),
                    ScreenData(Screen.Models, "Models", Icons.Default.Build),
                    ScreenData(Screen.Traces, "Traces", Icons.Default.Search),
                    ScreenData(Screen.Setup, "Setup", Icons.Default.Settings),
                ).forEach { item ->
                    NavigationBarItem(selected = currentScreen == item.screen, onClick = { viewModel.navigateTo(item.screen) }, icon = { Icon(item.icon, contentDescription = item.label) }, label = { Text(item.label, fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = colors.primary, selectedTextColor = colors.primary, unselectedIconColor = colors.mutedForeground, unselectedTextColor = colors.mutedForeground, indicatorColor = colors.primary.copy(alpha = 0.12f)))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (currentScreen) {
                Screen.Overview -> OverviewScreen(viewModel)
                Screen.Tokens -> TokensScreen(viewModel)
                Screen.Models -> ModelsScreen(viewModel)
                Screen.Traces -> TracesScreen(viewModel)
                Screen.Setup -> SetupScreen(viewModel)
                Screen.Playground -> PlaygroundScreen(viewModel)
                Screen.Config -> ConfigScreen(viewModel)
                Screen.Logs -> LogsScreen(viewModel)
                Screen.Metrics -> MetricsScreen(viewModel)
                Screen.Login -> {}
            }
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().background(colors.background.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary, modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

private data class ScreenData(val screen: Screen, val label: String, val icon: ImageVector)
