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
import com.freebuff.admin.api.ConnectionState
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

        LaunchedEffect(Unit) {
            if (!initialized) { initialized = true; viewModel.tryRestoreSession() }
        }

        when (connectionState) {
            is ConnectionState.Disconnected -> {
                LoginScreen(
                    onLogin = { url, token ->
                        loginError = null
                        scope.launch {
                            try { if (!viewModel.login(url, token)) loginError = "密码错误或服务器异常" }
                            catch (e: Exception) { loginError = "网络错误: ${e.message}" }
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
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = AppColors.Gray900,
                contentColor = AppColors.CardBg,
                shape = RoundedCornerShape(12.dp)
            ) { Text(msg) }
        }
    }
}

@Composable
private fun MainContent(viewModel: AppViewModel, currentScreen: Screen, isLoading: Boolean) {
    val colors = AppTheme.colors()

    Scaffold(
        containerColor = colors.background,
        topBar = {
            // iOS-style large title
            Surface(color = colors.surface, tonalElevation = 0.dp) {
                Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.logout() }) { Text("退出", color = colors.primary) }
                    }
                    Text(
                        text = screenTitle(currentScreen),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
                        color = colors.label,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = colors.surface, tonalElevation = 0.dp) {
                listOf(
                    NavItem(Screen.Overview, "总览", Icons.Default.Home),
                    NavItem(Screen.Tokens, "令牌", Icons.Default.List),
                    NavItem(Screen.Models, "模型", Icons.Default.Build),
                    NavItem(Screen.Traces, "追踪", Icons.Default.Search),
                    NavItem(Screen.Setup, "设置", Icons.Default.Settings),
                ).forEach { item ->
                    NavigationBarItem(
                        selected = currentScreen == item.screen,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.primary,
                            selectedTextColor = colors.primary,
                            unselectedIconColor = colors.secondaryLabel,
                            unselectedTextColor = colors.secondaryLabel,
                            indicatorColor = colors.primary.copy(alpha = 0.12f)
                        )
                    )
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

private fun screenTitle(screen: Screen): String = when (screen) {
    Screen.Overview -> "总览"
    Screen.Tokens -> "令牌管理"
    Screen.Models -> "模型"
    Screen.Traces -> "请求追踪"
    Screen.Setup -> "部署指南"
    Screen.Playground -> "对话测试"
    Screen.Config -> "配置编辑"
    Screen.Logs -> "日志"
    Screen.Metrics -> "指标"
    Screen.Login -> "登录"
}

private data class NavItem(val screen: Screen, val label: String, val icon: ImageVector)
