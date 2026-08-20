package com.freebuff.admin.ui

import com.freebuff.admin.api.AdminApi
import com.freebuff.admin.api.ConnectionState
import com.freebuff.admin.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel {
    val api = AdminApi()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.Overview)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _overview = MutableStateFlow(OverviewData())
    val overview: StateFlow<OverviewData> = _overview.asStateFlow()

    private val _tokens = MutableStateFlow(TokensData())
    val tokens: StateFlow<TokensData> = _tokens.asStateFlow()

    private val _models = MutableStateFlow(ModelsData())
    val models: StateFlow<ModelsData> = _models.asStateFlow()

    private val _traces = MutableStateFlow(TracesData())
    val traces: StateFlow<TracesData> = _traces.asStateFlow()

    private val _logs = MutableStateFlow(LogsData())
    val logs: StateFlow<LogsData> = _logs.asStateFlow()

    private val _metrics = MutableStateFlow(MetricsData())
    val metrics: StateFlow<MetricsData> = _metrics.asStateFlow()

    private val _setup = MutableStateFlow(SetupData())
    val setup: StateFlow<SetupData> = _setup.asStateFlow()

    private val _config = MutableStateFlow(ConfigData())
    val config: StateFlow<ConfigData> = _config.asStateFlow()

    private val _version = MutableStateFlow(VersionData())
    val version: StateFlow<VersionData> = _version.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _logFilter = MutableStateFlow(LogFilter())
    val logFilter: StateFlow<LogFilter> = _logFilter.asStateFlow()

    private var pollJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        refreshCurrentScreen()
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        scope.launch {
            delay(3000)
            _toastMessage.value = null
        }
    }

    fun dismissToast() {
        _toastMessage.value = null
    }

    suspend fun login(host: String, port: Int, password: String): Boolean {
        _isLoading.value = true
        try {
            api.configure(host, port, password)
            val success = api.login(password)
            if (success) {
                startPolling()
                refreshAll()
            }
            return success
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun logout() {
        pollJob?.cancel()
        api.logout()
    }

    private fun startPolling() {
        pollJob?.cancel()
        pollJob = scope.launch {
            while (isActive) {
                try {
                    refreshCurrentScreen()
                } catch (_: Exception) {}
                delay(5000)
            }
        }
    }

    suspend fun refreshAll() {
        _isLoading.value = true
        try {
            coroutineScope {
                launch { _overview.value = api.getOverview() }
                launch { _tokens.value = api.getTokens() }
                launch { _models.value = api.getModels() }
                launch { _traces.value = api.getTraces() }
                launch { _logs.value = api.getLogs() }
                launch { _metrics.value = api.getMetrics() }
                launch { _setup.value = api.getSetup() }
                launch { _config.value = api.getConfig() }
                launch { _version.value = api.getVersion() }
            }
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun refreshCurrentScreen() {
        when (_currentScreen.value) {
            Screen.Overview -> _overview.value = api.getOverview()
            Screen.Tokens -> _tokens.value = api.getTokens()
            Screen.Models -> _models.value = api.getModels()
            Screen.Traces -> _traces.value = api.getTraces()
            Screen.Logs -> {
                val f = _logFilter.value
                _logs.value = api.getLogs(f.level, f.message)
            }
            Screen.Metrics -> _metrics.value = api.getMetrics()
            Screen.Setup -> _setup.value = api.getSetup()
            Screen.Config -> _config.value = api.getConfig()
            else -> {}
        }
        _version.value = api.getVersion()
    }

    fun updateLogFilter(filter: LogFilter) {
        _logFilter.value = filter
        scope.launch {
            _logs.value = api.getLogs(filter.level, filter.message)
        }
    }

    suspend fun testToken(tokenId: Int): ActionResponse {
        val result = api.testToken(tokenId)
        showToast(result.message)
        return result
    }

    suspend fun testAllTokens(): ActionResponse {
        val result = api.testAllTokens()
        showToast(result.message)
        return result
    }

    suspend fun unlockToken(tokenId: Int): ActionResponse {
        val result = api.unlockToken(tokenId)
        showToast(result.message)
        return result
    }

    suspend fun finishToken(tokenId: Int): ActionResponse {
        val result = api.finishToken(tokenId)
        showToast(result.message)
        return result
    }

    suspend fun addToken(token: String): ActionResponse {
        val result = api.addToken(token)
        showToast(result.message)
        if (result.ok) refreshAll()
        return result
    }

    suspend fun removeToken(token: String): ActionResponse {
        val result = api.removeToken(token)
        showToast(result.message)
        if (result.ok) refreshAll()
        return result
    }

    suspend fun saveConfig(config: ConfigData): ActionResponse {
        val result = api.saveConfig(config)
        showToast(result.message)
        if (result.ok) _config.value = config
        return result
    }

    suspend fun switchMode(mode: String): ActionResponse {
        val result = api.switchMode(mode)
        showToast(result.message)
        if (result.ok) refreshAll()
        return result
    }

    suspend fun runSmoke(model: String, prompt: String): ActionResponse {
        return api.runSmoke(model, prompt)
    }

    suspend fun runDiag(): ActionResponse {
        val result = api.runDiag()
        showToast(result.message)
        return result
    }

    suspend fun reloadConfig(): ActionResponse {
        val result = api.reloadConfig()
        showToast(result.message)
        if (result.ok) refreshAll()
        return result
    }

    fun destroy() {
        pollJob?.cancel()
        scope.cancel()
    }
}

enum class Screen(val label: String, val icon: String) {
    Overview("总览", "📊"),
    Tokens("令牌", "🔑"),
    Models("模型", "🤖"),
    Traces("追踪", "🔍"),
    Playground("测试", "💬"),
    Config("配置", "⚙️"),
    Setup("部署", "📋"),
    Logs("日志", "📝"),
    Metrics("指标", "📈")
}

data class LogFilter(
    val level: String = "",
    val message: String = ""
)
