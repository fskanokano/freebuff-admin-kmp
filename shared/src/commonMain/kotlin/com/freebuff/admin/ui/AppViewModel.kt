package com.freebuff.admin.ui

import com.freebuff.admin.api.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class Screen {
    Overview,
    Tokens,
    Models,
    Traces,
    Setup,
    Playground,
    Config,
    Logs,
    Metrics,
}

class AppViewModel {
    val api = AdminApi()

    val currentScreen = MutableStateFlow(Screen.Overview)
    val isLoading = MutableStateFlow(false)
    val toastMessage = MutableStateFlow<String?>(null)
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Data caches
    val overviewData = MutableStateFlow<OverviewData?>(null)
    val tokensData = MutableStateFlow<TokensData?>(null)
    val modelsData = MutableStateFlow<ModelsData?>(null)
    val tracesData = MutableStateFlow<TracesData?>(null)
    val setupData = MutableStateFlow<SetupData?>(null)
    val configData = MutableStateFlow<ConfigData?>(null)
    val logsData = MutableStateFlow<LogsData?>(null)
    val metricsData = MutableStateFlow<MetricsData?>(null)
    val versionData = MutableStateFlow<VersionData?>(null)

    // Playground state
    val chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val isChatLoading = MutableStateFlow(false)
    val availableModels = MutableStateFlow<List<String>>(emptyList())
    val selectedModel = MutableStateFlow("")

    // Config editor state
    val configContent = MutableStateFlow("")

    // Logs filter state
    val logLevelFilter = MutableStateFlow("")
    val logMsgFilter = MutableStateFlow("")

    suspend fun login(host: String, port: Int, password: String): Boolean {
        isLoading.value = true
        try {
            val result = api.connect(host, port, password)
            if (result) {
                refreshCurrentScreen()
            }
            return result
        } catch (e: Exception) {
            return false
        } finally {
            isLoading.value = false
        }
    }

    fun logout() {
        api.disconnect()
        overviewData.value = null
        tokensData.value = null
        modelsData.value = null
        tracesData.value = null
        setupData.value = null
        configData.value = null
        logsData.value = null
        metricsData.value = null
        versionData.value = null
        chatMessages.value = emptyList()
    }

    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
        scope.launch { refreshCurrentScreen() }
    }

    fun refreshCurrentScreen() {
        scope.launch {
            try {
                when (currentScreen.value) {
                    Screen.Overview -> {
                        overviewData.value = api.getOverview()
                        versionData.value = api.getVersion()
                    }
                    Screen.Tokens -> tokensData.value = api.getTokens()
                    Screen.Models -> modelsData.value = api.getModels()
                    Screen.Traces -> tracesData.value = api.getTraces()
                    Screen.Setup -> {
                        setupData.value = api.getSetup()
                        availableModels.value = setupData.value?.models ?: emptyList()
                        if (selectedModel.value.isEmpty()) {
                            selectedModel.value = setupData.value?.model ?: ""
                        }
                    }
                    Screen.Config -> configData.value = api.getConfig()
                    Screen.Logs -> logsData.value = api.getLogs(logLevelFilter.value, logMsgFilter.value)
                    Screen.Metrics -> metricsData.value = api.getMetrics()
                    Screen.Playground -> {
                        if (modelsData.value == null) {
                            modelsData.value = api.getModels()
                        }
                        availableModels.value = modelsData.value?.models?.map { it.id } ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                toastMessage.value = "Refresh failed: ${e.message}"
            }
        }
    }

    fun showToast(message: String) {
        toastMessage.value = message
        scope.launch {
            delay(3000)
            if (toastMessage.value == message) {
                toastMessage.value = null
            }
        }
    }

    // Token actions
    fun testToken(id: Int) {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.testToken(id)
                showToast(result.message)
                tokensData.value = api.getTokens()
            } catch (e: Exception) {
                showToast("Test failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun testAllTokens() {
        scope.launch {
            isLoading.value = true
            try {
                val results = api.testAllTokens()
                val summary = results.joinToString("\n") { "Token ${it.token}: ${if (it.ok) "OK" else "FAIL"} - ${it.message}" }
                showToast("Test complete: ${results.count { it.ok }}/${results.size} passed")
                tokensData.value = api.getTokens()
            } catch (e: Exception) {
                showToast("Test failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun unlockToken(id: Int) {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.unlockToken(id)
                showToast(result.message)
                tokensData.value = api.getTokens()
            } catch (e: Exception) {
                showToast("Unlock failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun finishToken(id: Int) {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.finishToken(id)
                showToast(result.message)
                tokensData.value = api.getTokens()
            } catch (e: Exception) {
                showToast("Finish failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addToken(token: String) {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.addToken(token)
                showToast(result.message)
                tokensData.value = api.getTokens()
            } catch (e: Exception) {
                showToast("Add failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun removeToken() {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.removeToken()
                showToast(result.message)
                tokensData.value = api.getTokens()
            } catch (e: Exception) {
                showToast("Remove failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun switchMode(mode: String) {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.switchMode(mode)
                showToast(result.message)
                overviewData.value = api.getOverview()
                tokensData.value = api.getTokens()
            } catch (e: Exception) {
                showToast("Mode switch failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun saveConfig(content: String) {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.saveConfig(content)
                showToast(result.message)
                configData.value = api.getConfig()
            } catch (e: Exception) {
                showToast("Save failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun reloadConfig() {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.reloadConfig()
                showToast(result.message)
                configData.value = api.getConfig()
            } catch (e: Exception) {
                showToast("Reload failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun runSmoke(model: String) {
        scope.launch {
            isLoading.value = true
            try {
                val result = api.runSmoke(model)
                if (result.ok) {
                    showToast("Smoke test OK: ${result.model} in ${result.ms}ms")
                } else {
                    showToast("Smoke test failed")
                }
            } catch (e: Exception) {
                showToast("Smoke test failed: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    // Playground
    fun sendChatMessage(content: String) {
        if (content.isBlank()) return
        val model = selectedModel.value
        if (model.isBlank()) {
            showToast("Please select a model first")
            return
        }

        val messages = chatMessages.value.toMutableList()
        messages.add(ChatMessage("user", content))
        chatMessages.value = messages
        isChatLoading.value = true

        val responseBuilder = StringBuilder()
        api.chatStream(
            model = model,
            messages = messages,
            onChunk = { chunk ->
                responseBuilder.append(chunk)
                val currentMessages = chatMessages.value.toMutableList()
                // Update or add assistant message
                val lastAssistant = currentMessages.lastOrNull { it.role == "assistant" }
                if (lastAssistant != null) {
                    currentMessages[currentMessages.size - 1] = ChatMessage("assistant", responseBuilder.toString())
                } else {
                    currentMessages.add(ChatMessage("assistant", responseBuilder.toString()))
                }
                chatMessages.value = currentMessages
            },
            onDone = {
                isChatLoading.value = false
                if (responseBuilder.isEmpty()) {
                    val currentMessages = chatMessages.value.toMutableList()
                    currentMessages.add(ChatMessage("assistant", "(empty response)"))
                    chatMessages.value = currentMessages
                }
            },
            onError = { error ->
                isChatLoading.value = false
                showToast("Chat error: $error")
            }
        )
    }

    fun clearChat() {
        chatMessages.value = emptyList()
    }

    // Logs filter
    fun setLogLevelFilter(level: String) {
        logLevelFilter.value = level
        scope.launch { refreshCurrentScreen() }
    }

    fun setLogMsgFilter(msg: String) {
        logMsgFilter.value = msg
    }

    fun searchLogs() {
        scope.launch { refreshCurrentScreen() }
    }

    fun destroy() {
        api.close()
        scope.cancel()
    }
}
