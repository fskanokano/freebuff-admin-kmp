package com.freebuff.admin.api

import com.freebuff.admin.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class AdminApi {
    private var baseUrl: String = ""
    private var adminToken: String = ""
    private var isAuthenticated: Boolean = false

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private var client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
        }
    }

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    fun configure(host: String, port: Int = 3457, token: String = "") {
        baseUrl = "http://$host:$port"
        adminToken = token
    }

    suspend fun login(password: String): Boolean {
        return try {
            val response = client.submitForm(
                url = "$baseUrl/admin/login",
                formParameters = parameters {
                    append("token", password)
                }
            )
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Found) {
                isAuthenticated = true
                adminToken = password
                _connectionState.value = ConnectionState.Connected(baseUrl)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error(e.message ?: "连接失败")
            false
        }
    }

    suspend fun logout() {
        try {
            client.post("$baseUrl/admin/logout")
        } catch (_: Exception) {}
        isAuthenticated = false
        adminToken = ""
        _connectionState.value = ConnectionState.Disconnected
    }

    private fun HttpRequestBuilder.auth() {
        if (adminToken.isNotEmpty()) {
            header("Authorization", "Bearer $adminToken")
        }
    }

    suspend fun getOverview(): OverviewData {
        return try {
            client.get("$baseUrl/admin/api/overview") { auth() }.body()
        } catch (e: Exception) {
            OverviewData()
        }
    }

    suspend fun getTokens(): TokensData {
        return try {
            client.get("$baseUrl/admin/api/tokens") { auth() }.body()
        } catch (e: Exception) {
            TokensData()
        }
    }

    suspend fun getModels(): ModelsData {
        return try {
            client.get("$baseUrl/admin/api/models") { auth() }.body()
        } catch (e: Exception) {
            ModelsData()
        }
    }

    suspend fun getTraces(): TracesData {
        return try {
            client.get("$baseUrl/admin/api/traces") { auth() }.body()
        } catch (e: Exception) {
            TracesData()
        }
    }

    suspend fun getLogs(level: String = "", msg: String = ""): LogsData {
        return try {
            val params = buildString {
                if (level.isNotEmpty()) append("level=$level")
                if (msg.isNotEmpty()) {
                    if (isNotEmpty()) append("&")
                    append("msg=$msg")
                }
            }
            val url = if (params.isNotEmpty()) "$baseUrl/admin/api/logs?$params" else "$baseUrl/admin/api/logs"
            client.get(url) { auth() }.body()
        } catch (e: Exception) {
            LogsData()
        }
    }

    suspend fun getMetrics(): MetricsData {
        return try {
            client.get("$baseUrl/admin/api/metrics") { auth() }.body()
        } catch (e: Exception) {
            MetricsData()
        }
    }

    suspend fun getSetup(): SetupData {
        return try {
            client.get("$baseUrl/admin/api/setup") { auth() }.body()
        } catch (e: Exception) {
            SetupData()
        }
    }

    suspend fun getConfig(): ConfigData {
        return try {
            client.get("$baseUrl/admin/api/config") { auth() }.body()
        } catch (e: Exception) {
            ConfigData()
        }
    }

    suspend fun saveConfig(config: ConfigData): ActionResponse {
        return try {
            client.post("$baseUrl/admin/config") {
                auth()
                contentType(ContentType.Application.Json)
                setBody(config)
            }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "保存失败")
        }
    }

    suspend fun getVersion(): VersionData {
        return try {
            client.get("$baseUrl/admin/api/version") { auth() }.body()
        } catch (e: Exception) {
            VersionData()
        }
    }

    suspend fun testToken(tokenId: Int): ActionResponse {
        return try {
            client.post("$baseUrl/admin/tokens/$tokenId/test") { auth() }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "测试失败")
        }
    }

    suspend fun testAllTokens(): ActionResponse {
        return try {
            client.post("$baseUrl/admin/tokens/test-all") { auth() }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "测试失败")
        }
    }

    suspend fun unlockToken(tokenId: Int): ActionResponse {
        return try {
            client.post("$baseUrl/admin/tokens/$tokenId/unlock") { auth() }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "解锁失败")
        }
    }

    suspend fun finishToken(tokenId: Int): ActionResponse {
        return try {
            client.post("$baseUrl/admin/tokens/$tokenId/finish") { auth() }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "结束失败")
        }
    }

    suspend fun addToken(token: String): ActionResponse {
        return try {
            client.post("$baseUrl/admin/tokens/add") {
                auth()
                contentType(ContentType.Application.Json)
                setBody(mapOf("token" to token))
            }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "添加失败")
        }
    }

    suspend fun removeToken(token: String): ActionResponse {
        return try {
            client.post("$baseUrl/admin/tokens/remove") {
                auth()
                contentType(ContentType.Application.Json)
                setBody(mapOf("token" to token))
            }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "移除失败")
        }
    }

    suspend fun switchMode(mode: String): ActionResponse {
        return try {
            client.post("$baseUrl/admin/mode") {
                auth()
                contentType(ContentType.Application.Json)
                setBody(mapOf("mode" to mode))
            }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "切换失败")
        }
    }

    suspend fun runSmoke(model: String, prompt: String): ActionResponse {
        return try {
            client.post("$baseUrl/admin/smoke") {
                auth()
                contentType(ContentType.Application.Json)
                setBody(mapOf("model" to model, "prompt" to prompt))
            }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "测试失败")
        }
    }

    suspend fun runDiag(): ActionResponse {
        return try {
            client.post("$baseUrl/admin/diag") { auth() }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "诊断失败")
        }
    }

    suspend fun reloadConfig(): ActionResponse {
        return try {
            client.post("$baseUrl/admin/reload") { auth() }.body()
        } catch (e: Exception) {
            ActionResponse(ok = false, message = e.message ?: "重载失败")
        }
    }
}

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data class Connected(val url: String) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
