package com.freebuff.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.freebuff.admin.ui.components.*
import com.freebuff.admin.ui.screens.*
import com.freebuff.admin.ui.theme.AppColors
import com.freebuff.admin.ui.theme.AppTheme
import com.freebuff.admin.ui.theme.FreebuffTheme
import kotlinx.coroutines.launch

@Composable
fun App(viewModel: AppViewModel) {
    FreebuffTheme {
        val connectionState by viewModel.api.connectionState.collectAsState()
        val currentScreen by viewModel.currentScreen.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val toastMessage by viewModel.toastMessage.collectAsState()

        var loginError by remember { mutableStateOf<String?>(null) }

        when (val state = connectionState) {
            is ConnectionState.Disconnected -> {
                LoginScreen(
                    onLogin = { host, port, password ->
                        loginError = null
                        viewModel.scope.launch {
                            val success = viewModel.login(host, port, password)
                            if (!success) {
                                loginError = "连接失败，请检查地址和密码"
                            }
                        }
                    },
                    isLoading = isLoading,
                    error = loginError
                )
            }
            is ConnectionState.Connected -> {
                MainContent(
                    viewModel = viewModel,
                    currentScreen = currentScreen,
                    isLoading = isLoading,
                    toastMessage = toastMessage
                )
            }
            is ConnectionState.Error -> {
                LoginScreen(
                    onLogin = { host, port, password ->
                        loginError = null
                        viewModel.scope.launch {
                            val success = viewModel.login(host, port, password)
                            if (!success) {
                                loginError = state.message
                            }
                        }
                    },
                    isLoading = isLoading,
                    error = state.message
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModel: AppViewModel,
    currentScreen: Screen,
    isLoading: Boolean,
    toastMessage: String?
) {
    val colors = AppTheme.colors()
    val scope = rememberCoroutineScope()

    // Data states
    val overview by viewModel.overview.collectAsState()
    val tokens by viewModel.tokens.collectAsState()
    val models by viewModel.models.collectAsState()
    val traces by viewModel.traces.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val metrics by viewModel.metrics.collectAsState()
    val setup by viewModel.setup.collectAsState()
    val config by viewModel.config.collectAsState()
    val version by viewModel.version.collectAsState()
    val logFilter by viewModel.logFilter.collectAsState()

    var configSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🚀", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Freebuff Proxy",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.onSurface
                            )
                            if (version.current_version.isNotEmpty()) {
                                Text(
                                    text = "v${version.current_version}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.mutedForeground
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Update badge
                    if (version.has_update) {
                        StatusBadge(
                            text = "v${version.latest_version}",
                            color = AppColors.Amber
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Logout
                    TextButton(
                        onClick = {
                            scope.launch { viewModel.logout() }
                        }
                    ) {
                        Text(
                            "退出",
                            color = colors.destructive,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 0.dp
            ) {
                Screen.entries.take(5).forEach { screen ->
                    NavigationBarItem(
                        icon = { Text(screen.icon, fontSize = 18.sp) },
                        label = {
                            Text(
                                screen.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = currentScreen == screen,
                        onClick = { viewModel.navigateTo(screen) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.primary,
                            selectedTextColor = colors.primary,
                            unselectedIconColor = colors.mutedForeground,
                            unselectedTextColor = colors.mutedForeground,
                            indicatorColor = colors.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        containerColor = colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.Overview -> OverviewScreen(
                    data = overview,
                    onSmokeTest = {
                        scope.launch {
                            viewModel.runSmoke(setup.model.ifEmpty { "deepseek/deepseek-v4-flash" }, "Say hello in one word")
                        }
                    }
                )
                Screen.Tokens -> TokensScreen(
                    data = tokens,
                    onTestToken = { scope.launch { viewModel.testToken(it) } },
                    onTestAll = { scope.launch { viewModel.testAllTokens() } },
                    onUnlockToken = { scope.launch { viewModel.unlockToken(it) } },
                    onFinishToken = { scope.launch { viewModel.finishToken(it) } },
                    onAddToken = { /* Dialog handled in screen */ },
                    onRemoveToken = { scope.launch { viewModel.removeToken(it.toString()) } },
                    onSwitchMode = { scope.launch { viewModel.switchMode(it) } }
                )
                Screen.Models -> ModelsScreen(data = models)
                Screen.Traces -> TracesScreen(data = traces)
                Screen.Playground -> PlaygroundScreen(
                    setupData = setup,
                    onSend = { model, prompt ->
                        scope.launch {
                            viewModel.runSmoke(model, prompt)
                        }
                    },
                    isLoading = isLoading
                )
                Screen.Config -> ConfigScreen(
                    data = config,
                    onSave = { cfg ->
                        scope.launch {
                            configSaving = true
                            viewModel.saveConfig(cfg)
                            configSaving = false
                        }
                    },
                    onReload = { scope.launch { viewModel.reloadConfig() } },
                    isSaving = configSaving
                )
                Screen.Setup -> SetupScreen(data = setup)
                Screen.Logs -> LogsScreen(
                    data = logs,
                    filter = logFilter,
                    onFilterChange = { viewModel.updateLogFilter(it) }
                )
                Screen.Metrics -> MetricsScreen(data = metrics)
            }

            // Loading overlay
            if (isLoading && currentScreen != Screen.Overview) {
                LoadingOverlay(colors)
            }
        }
    }

    // Toast
    Toast(
        message = toastMessage ?: "",
        onDismiss = { viewModel.dismissToast() }
    )
}
