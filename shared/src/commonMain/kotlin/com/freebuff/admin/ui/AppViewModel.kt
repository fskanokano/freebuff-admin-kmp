package com.freebuff.admin.ui

import com.freebuff.admin.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val _chatMessages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatMessages: StateFlow<List<Pair<String, String>>> = _chatMessages

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading: StateFlow<Boolean> = _chatLoading

    private val _selectedModel = MutableStateFlow("")
    val selectedModel: StateFlow<String> = _selectedModel

    // ── Persistence (set from App.kt) ──
    var savedUrl: String = ""
    var savedCookie: String = ""

    fun tryRestoreSession(): Boolean {
        if (savedUrl.isNotEmpty() && savedCookie.isNotEmpty()) {
            api.restoreSession(savedUrl, savedCookie)
            _currentScreen.value = Screen.Overview
            scope.launch { refreshCurrentScreen() }
            return true
        }
        return false
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        scope.launch { refreshCurrentScreen() }
    }

    private suspend fun refreshCurrentScreen() {
        try {
            when (_currentScreen.value) {
                Screen.Overview -> _overview.value = api.getOverview()
                Screen.Tokens -> _tokens.value = api.getTokens()
                Screen.Models -> _models.value = api.getModels()
                Screen.Traces -> _traces.value = api.getTraces()
                Screen.Setup -> _setup.value = api.getSetup()
                Screen.Config -> _config.value = api.getConfig()
                Screen.Logs -> _logs.value = api.getLogs()
                Screen.Metrics -> _metrics.value = api.getMetrics()
                else -> {}
            }
        } catch (e: Exception) {
            _toastMessage.value = "Load failed: ${e.message}"
        }
    }

    fun refresh() { scope.launch { refreshCurrentScreen() } }
    fun dismissToast() { _toastMessage.value = null }

    suspend fun login(serverUrl: String, password: String): Boolean {
        _isLoading.value = true
        val result = api.login(serverUrl, password)
        _isLoading.value = false
        if (result) {
            savedUrl = serverUrl
            savedCookie = api.getSessionCookie()
            _currentScreen.value = Screen.Overview
            scope.launch { refreshCurrentScreen() }
        }
        return result
    }

    fun logout() {
        api.logout()
        _currentScreen.value = Screen.Login
        _overview.value = null; _tokens.value = null; _models.value = null
        _traces.value = null; _setup.value = null; _config.value = null
        _logs.value = null; _metrics.value = null
    }

    fun testToken(id: Int) { scope.launch { api.testToken(id); _tokens.value = api.getTokens() } }
    fun testAllTokens() { scope.launch { api.testAllTokens(); _tokens.value = api.getTokens() } }
    fun unlockToken(id: Int) { scope.launch { api.unlockToken(id); _tokens.value = api.getTokens() } }
    fun finishToken(id: Int) { scope.launch { api.finishToken(id); _tokens.value = api.getTokens() } }
    fun addToken() { scope.launch { api.addToken(); _tokens.value = api.getTokens() } }
    fun removeToken() { scope.launch { api.removeToken(); _tokens.value = api.getTokens() } }
    fun switchMode() { scope.launch { api.switchMode(); refresh() } }

    fun saveConfig(content: String) {
        scope.launch {
            api.saveConfig(content)
            _config.value = api.getConfig()
            _toastMessage.value = "Config saved"
        }
    }

    fun reloadConfig() {
        scope.launch {
            api.reloadConfig()
            _config.value = api.getConfig()
            _toastMessage.value = "Config reloaded"
        }
    }

    fun setSelectedModel(model: String) { _selectedModel.value = model }

    fun sendChat(prompt: String) {
        val model = _selectedModel.value.ifEmpty { "auto" }
        val messages = _chatMessages.value.toMutableList()
        messages.add(Pair("user", prompt))
        _chatMessages.value = messages
        _chatLoading.value = true
        scope.launch {
            try {
                val msgJson = messages.joinToString(",", "[", "]") { (role, content) ->
                    """{"role":"$role","content":"$content"}"""
                }
                val response = api.chat(model, msgJson)
                _chatMessages.value = _chatMessages.value + Pair("assistant", response)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + Pair("assistant", "Error: ${e.message}")
            }
            _chatLoading.value = false
        }
    }

    fun clearChat() { _chatMessages.value = emptyList() }

    fun destroy() { api.close() }
}
