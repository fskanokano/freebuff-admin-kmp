package com.freebuff.admin.ui

import com.freebuff.admin.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class Screen { Login, Overview, Tokens, Models, Traces, Setup, Playground, Config, Logs, Metrics }

class AppViewModel {
    val api = AdminApi()

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _currentScreen = MutableStateFlow(Screen.Overview)
    val currentScreen: StateFlow<Screen> = _currentScreen

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    // Data states
    private val _overview = MutableStateFlow<OverviewData?>(null)
    val overview: StateFlow<OverviewData?> = _overview

    private val _tokens = MutableStateFlow<TokensData?>(null)
    val tokens: StateFlow<TokensData?> = _tokens

    private val _models = MutableStateFlow<ModelsData?>(null)
    val models: StateFlow<ModelsData?> = _models

    private val _traces = MutableStateFlow<TracesData?>(null)
    val traces: StateFlow<TracesData?> = _traces

    private val _setup = MutableStateFlow<SetupData?>(null)
    val setup: StateFlow<SetupData?> = _setup

    private val _config = MutableStateFlow<ConfigData?>(null)
    val config: StateFlow<ConfigData?> = _config

    private val _logs = MutableStateFlow<LogsData?>(null)
    val logs: StateFlow<LogsData?> = _logs

    private val _metrics = MutableStateFlow<MetricsData?>(null)
    val metrics: StateFlow<MetricsData?> = _metrics

    private val _version = MutableStateFlow<VersionData?>(null)
    val version: StateFlow<VersionData?> = _version

    private val _smokeResult = MutableStateFlow<String?>(null)
    val smokeResult: StateFlow<String?> = _smokeResult

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading: StateFlow<Boolean> = _chatLoading

    private val _selectedModel = MutableStateFlow("")
    val selectedModel: StateFlow<String> = _selectedModel

    // ── Navigation ──

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        scope.launch { refreshCurrentScreen() }
    }

    // ── Data loading ──

    private suspend fun refreshCurrentScreen() {
        try {
            when (_currentScreen.value) {
                Screen.Overview -> _overview.value = api.getOverview()
                Screen.Tokens -> _tokens.value = api.getTokens()
                Screen.Models -> {
                    _models.value = api.getModels()
                    _version.value = api.getVersion()
                }
                Screen.Traces -> _traces.value = api.getTraces()
                Screen.Setup -> _setup.value = api.getSetup()
                Screen.Config -> _config.value = api.getConfig()
                Screen.Logs -> _logs.value = api.getLogs()
                Screen.Metrics -> _metrics.value = api.getMetrics()
                else -> {}
            }
        } catch (e: Exception) {
            _toastMessage.value = "Failed to load: ${e.message}"
        }
    }

    fun refresh() {
        scope.launch { refreshCurrentScreen() }
    }

    fun dismissToast() {
        _toastMessage.value = null
    }

    // ── Auth ──

    suspend fun login(host: String, port: Int, password: String): Boolean {
        _isLoading.value = true
        val result = api.login(host, port, password)
        _isLoading.value = false
        if (result) {
            _currentScreen.value = Screen.Overview
            scope.launch { refreshCurrentScreen() }
        }
        return result
    }

    fun logout() {
        api.logout()
        _currentScreen.value = Screen.Login
        _overview.value = null
        _tokens.value = null
        _models.value = null
        _traces.value = null
        _setup.value = null
        _config.value = null
        _logs.value = null
        _metrics.value = null
    }

    // ── Token actions ──

    fun testToken(id: String) { scope.launch { api.testToken(id); refresh() } }
    fun testAllTokens() { scope.launch { api.testAllTokens(); refresh() } }
    fun unlockToken(id: String) { scope.launch { api.unlockToken(id); refresh() } }
    fun finishToken(id: String) { scope.launch { api.finishToken(id); refresh() } }
    fun addToken() { scope.launch { api.addToken(); refresh() } }
    fun removeToken() { scope.launch { api.removeToken(); refresh() } }
    fun switchMode() { scope.launch { api.switchMode(); refresh() } }

    // ── Config actions ──

    fun saveConfig(content: String) {
        scope.launch {
            api.saveConfig(content)
            _config.value = api.getConfig()
            _toastMessage.value = "Configuration saved"
        }
    }

    fun reloadConfig() {
        scope.launch {
            api.reloadConfig()
            _config.value = api.getConfig()
            _toastMessage.value = "Configuration reloaded"
        }
    }

    // ── Smoke test ──

    fun runSmokeTest(model: String, prompt: String) {
        scope.launch {
            try {
                val result = api.smokeTest(model, prompt, false)
                _smokeResult.value = result.toString()
            } catch (e: Exception) {
                _toastMessage.value = "Smoke test failed: ${e.message}"
            }
        }
    }

    // ── Playground ──

    fun setSelectedModel(model: String) { _selectedModel.value = model }

    fun sendChat(prompt: String) {
        val model = _selectedModel.value.ifEmpty { "auto" }
        val messages = _chatMessages.value.toMutableList()
        messages.add(ChatMessage("user", prompt))
        _chatMessages.value = messages
        _chatLoading.value = true

        scope.launch {
            try {
                val response = api.chat(model, _chatMessages.value)
                _chatMessages.value = _chatMessages.value + ChatMessage("assistant", response)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage("assistant", "Error: ${e.message}")
            }
            _chatLoading.value = false
        }
    }

    fun clearChat() { _chatMessages.value = emptyList() }

    // ── Diagnostics ──

    fun runDiagnostics() {
        scope.launch {
            try {
                val result = api.runDiagnostics()
                _toastMessage.value = result.toString()
            } catch (e: Exception) {
                _toastMessage.value = "Diagnostics failed: ${e.message}"
            }
        }
    }

    fun destroy() {
        api.close()
    }
}
