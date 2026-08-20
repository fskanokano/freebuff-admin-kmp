package com.freebuff.admin.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.serialization.json.*
import kotlinx.serialization.json.*

// --- Auth state ---

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data class Connected(val url: String) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

// --- Real API response models (match server dashboard.go) ---

@kotlinx.serialization.Serializable
data class OverviewData(
    val mode: String = "",
    val in_bridge: Boolean = false,
    val bridge_tokens: Int = 0,
    val models: List<String> = emptyList(),
    val model_count: Int = 0,
    val uptime: String = "",
    val safe_mode: Boolean = false,
    val max_messages_per_day: Int = 0,
    val transient_retries: Long = 0,
    val fingerprint_rotations: Long = 0,
    val tokens: List<TokenCard> = emptyList(),
    val has_tokens: Boolean = false,
)

@kotlinx.serialization.Serializable
data class TokenCard(
    val index: Int = 0,
    val session_status: String = "",
    val queue_position: Int = 0,
    val queue_depth: Int = 0,
    val active_runs: Int = 0,
    val requests: Int = 0,
    val messages_24h: Int = 0,
    val daily_limit: Int = 0,
    val usage_pct: Int = 0,
    val risk_level: String = "",
    val cooldown_active: Boolean = false,
    val cooldown_until: String = "",
    val transient_retries: Long = 0,
    val has_standing: Boolean = false,
    val standing_level: String = "",
    val standing_label: String = "",
    val standing_score: Double = 0.0,
    val standing_next_level: String = "",
    val standing_next_level_at: String = "",
)

@kotlinx.serialization.Serializable
data class TokensData(
    val mode: String = "",
    val in_bridge: Boolean = false,
    val bridge_tokens: Int = 0,
    val token_count: Int = 0,
    val tokens: List<TokenDetail> = emptyList(),
    val has_tokens: Boolean = false,
)

@kotlinx.serialization.Serializable
data class TokenDetail(
    val index: Int = 0,
    val session_status: String = "",
    val session_instance: String = "",
    val queue_position: Int = 0,
    val queue_depth: Int = 0,
    val active_runs: Int = 0,
    val requests: Int = 0,
    val messages_24h: Int = 0,
    val daily_limit: Int = 0,
    val usage_pct: Int = 0,
    val risk_level: String = "",
    val cooldown_active: Boolean = false,
    val cooldown_until: String = "",
    val transient_retries: Long = 0,
    val has_standing: Boolean = false,
    val standing_level: String = "",
    val standing_label: String = "",
    val standing_score: Double = 0.0,
    val standing_next_level: String = "",
    val standing_next_level_at: String = "",
    val quota: List<QuotaRow> = emptyList(),
    val has_quota: Boolean = false,
)

@kotlinx.serialization.Serializable
data class QuotaRow(
    val model: String = "",
    val limit: String = "",
    val recent: String = "",
    val period: String = "",
    val reset_at: String = "",
    val reset_at_utc: String = "",
    val resets_in: String = "",
    val entitled: String = "",
    val has_entitlement: Boolean = false,
    val usage_pct: Int = 0,
    val near_limit: Boolean = false,
    val has_bar: Boolean = false,
)

@kotlinx.serialization.Serializable
data class ModelsData(
    val models: List<ModelRow> = emptyList(),
    val count: Int = 0,
    val agents: Int = 0,
    val aliases: List<AliasRow> = emptyList(),
    val has_aliases: Boolean = false,
)

@kotlinx.serialization.Serializable
data class ModelRow(
    val id: String = "",
    val agent: String = "",
)

@kotlinx.serialization.Serializable
data class AliasRow(
    val alias: String = "",
    val real: String = "",
)

@kotlinx.serialization.Serializable
data class TracesData(
    val enabled: Boolean = false,
    val traces: List<TraceEntry> = emptyList(),
)

@kotlinx.serialization.Serializable
data class TraceEntry(
    val time: String = "",
    val token: String = "",
    val model: String = "",
    val status: String = "",
    val ms: String = "",
    val error: String = "",
    val phases: List<PhaseKV> = emptyList(),
)

@kotlinx.serialization.Serializable
data class PhaseKV(
    val name: String = "",
    val ms: Long = 0,
)

@kotlinx.serialization.Serializable
data class ConfigData(
    val env_content: String = "",
    val has_env_file: Boolean = false,
    val effective: List<ConfigKV> = emptyList(),
)

@kotlinx.serialization.Serializable
data class ConfigKV(
    val key: String = "",
    val value: String = "",
    val secret: Boolean = false,
)

@kotlinx.serialization.Serializable
data class SetupData(
    val base_url: String = "",
    val key_hint: String = "",
    val model: String = "",
    val models: List<String> = emptyList(),
    val mode: String = "",
    val bridge: Boolean = false,
    val bridge_tokens: Int = 0,
    val token_count: Int = 0,
    val has_tokens: Boolean = false,
)

@kotlinx.serialization.Serializable
data class LogsData(
    val enabled: Boolean = false,
    val level: String = "",
    val msg: String = "",
    val has_filter: Boolean = false,
    val entries: List<LogEntry> = emptyList(),
)

@kotlinx.serialization.Serializable
data class LogEntry(
    val time: String = "",
    val level: String = "",
    val message: String = "",
    val fields: String = "",
)

@kotlinx.serialization.Serializable
data class MetricsData(
    val transient_retries: Long = 0,
    val fingerprint_rotations: Long = 0,
    val requests_total: Long = 0,
    val models: Int = 0,
    val sample_count: Int = 0,
    val requests_spark: String = "",
    val retries_spark: String = "",
    val requests_trend: MetricTrend = MetricTrend(),
    val retries_trend: MetricTrend = MetricTrend(),
    val per_tokens: List<PerTokenMetrics> = emptyList(),
)

@kotlinx.serialization.Serializable
data class MetricTrend(
    val direction: String = "flat",
    val percentage: Double = 0.0,
)

@kotlinx.serialization.Serializable
data class PerTokenMetrics(
    val token: Int = 0,
    val requests_24h: Int = 0,
    val transient_retries: Long = 0,
    val fingerprint_rotations: Long = 0,
    val spend_day: Long = 0,
    val risk_level: String = "",
)

@kotlinx.serialization.Serializable
data class VersionData(
    val current_version: String = "",
    val has_update: Boolean = false,
    val latest_version: String = "",
    val update_url: String = "",
)

@kotlinx.serialization.Serializable
data class ActionResult(
    val ok: Boolean = false,
    val message: String = "",
)

@kotlinx.serialization.Serializable
data class TestResult(
    val token: Int = 0,
    val ok: Boolean = false,
    val message: String = "",
    val instance_id: String = "",
)

@kotlinx.serialization.Serializable
data class SmokeResult(
    val ok: Boolean = false,
    val model: String = "",
    val token: String = "",
    val ms: Long = 0,
    val preview: String = "",
    val phases: List<PhaseKV> = emptyList(),
)

@kotlinx.serialization.Serializable
data class DiagCheck(
    val name: String = "",
    val ok: Boolean = false,
    val warn: Boolean = false,
    val message: String = "",
)

@kotlinx.serialization.Serializable
data class DiagResult(
    val checks: List<DiagCheck> = emptyList(),
)

@kotlinx.serialization.Serializable
data class LoginStatus(
    val id: String = "",
    val code: String = "",
    val url: String = "",
    val done: Boolean = false,
    val error: String = "",
    val token: String = "",
    val index: Int = -1,
)

// --- API Client ---

class AdminApi {
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private var client: HttpClient? = null
    private var baseUrl: String = ""
    private var adminToken: String = ""

    // Store cookie from login response
    private var sessionCookie: String = ""

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun connect(host: String, port: Int, password: String): Boolean {
        baseUrl = "http://$host:$port"
        adminToken = password

        client?.close()
        client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
            }
            followRedirects = false
        }

        return try {
            val loginResponse = client!!.submitForm(
                url = "$baseUrl/admin/login",
                formParameters = parameters {
                    append("token", password)
                }
            )

            // Extract Set-Cookie header
            val setCookie = loginResponse.headers["Set-Cookie"] ?: ""
            val cookieValue = setCookie.split(";").firstOrNull { it.trim().startsWith("fb_admin=") }
            if (cookieValue != null) {
                sessionCookie = cookieValue.trim()
                _connectionState.value = ConnectionState.Connected(baseUrl)
                true
            } else {
                // Check if redirected to dashboard (means ADMIN_TOKEN is empty)
                if (loginResponse.status == HttpStatusCode.Found) {
                    sessionCookie = setCookie.split(";").firstOrNull { it.trim().startsWith("fb_admin=") } ?: ""
                    _connectionState.value = ConnectionState.Connected(baseUrl)
                    true
                } else {
                    _connectionState.value = ConnectionState.Error("Invalid admin token")
                    false
                }
            }
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error("Connection failed: ${e.message}")
            false
        }
    }

    fun disconnect() {
        sessionCookie = ""
        _connectionState.value = ConnectionState.Disconnected
        client?.close()
        client = null
    }

    private fun HttpRequestBuilder.adminCookie() {
        if (sessionCookie.isNotEmpty()) {
            header("Cookie", sessionCookie)
        }
        // CSRF bypass for POST requests from mobile app
        header("Origin", baseUrl)
        header("Sec-Fetch-Site", "same-origin")
    }

    // --- GET endpoints ---

    suspend fun getOverview(): OverviewData = get("overview")
    suspend fun getTokens(): TokensData = get("tokens")
    suspend fun getModels(): ModelsData = get("models")
    suspend fun getTraces(): TracesData = get("traces")
    suspend fun getSetup(): SetupData = get("setup")
    suspend fun getConfig(): ConfigData = get("config")
    suspend fun getMetrics(): MetricsData = get("metrics")
    suspend fun getVersion(): VersionData = get("version")

    suspend fun getLogs(level: String = "", msg: String = ""): LogsData {
        val params = mutableListOf<String>()
        if (level.isNotEmpty()) params.add("level=$level")
        if (msg.isNotEmpty()) params.add("msg=$msg")
        val query = if (params.isNotEmpty()) "?${params.joinToString("&")}" else ""
        return get("logs$query")
    }

    private suspend inline fun <reified T> get(path: String): T {
        val response = client!!.get("$baseUrl/admin/api/$path") {
            adminCookie()
        }
        return json.decodeFromString(response.bodyAsText())
    }

    // --- POST endpoints ---

    suspend fun saveConfig(envContent: String): ActionResult {
        val body = buildJsonObject {
            put("env_content", envContent)
        }
        return postBody("config", body.toString())
    }

    suspend fun unlockToken(id: Int): ActionResult = post("tokens/$id/unlock")
    suspend fun finishToken(id: Int): ActionResult = post("tokens/$id/finish")

    suspend fun testToken(id: Int): ActionResult = post("tokens/$id/test")
    suspend fun testAllTokens(): List<TestResult> {
        val client = client ?: return emptyList()
        val response = client.submitForm(
            url = "$baseUrl/admin/tokens/test-all",
            formParameters = parameters { }
        ) {
            adminCookie()
        }
        val text = response.bodyAsText()
        // Test-all returns one result per token, may be multiple JSON objects
        // But server wraps in array? Let's parse as single for now
        return try {
            json.decodeFromString<List<TestResult>>(text)
        } catch (e: Exception) {
            try {
                listOf(json.decodeFromString(text))
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    suspend fun addToken(token: String): ActionResult {
        val body = buildJsonObject { put("token", token) }
        return postBody("tokens/add", body.toString())
    }

    suspend fun removeToken(): ActionResult = post("tokens/remove")

    suspend fun switchMode(mode: String): ActionResult {
        val body = buildJsonObject { put("mode", mode) }
        return postBody("mode", body.toString())
    }

    suspend fun runDiag(): DiagResult {
        val client = client ?: return DiagResult()
        val response = client.submitForm(
            url = "$baseUrl/admin/diag",
            formParameters = parameters { }
        ) {
            adminCookie()
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun runSmoke(model: String, prompt: String = "", stream: Boolean = false): SmokeResult {
        val body = buildJsonObject {
            put("model", model)
            put("prompt", prompt)
            put("stream", stream)
        }
        val client = client ?: return SmokeResult()
        val response = client.submitForm(
            url = "$baseUrl/admin/smoke",
            formParameters = parameters {
                append("model", model)
                if (prompt.isNotEmpty()) append("prompt", prompt)
                append("stream", stream.toString())
            }
        ) {
            adminCookie()
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun reloadConfig(): ActionResult = post("reload")

    private suspend fun post(path: String): ActionResult {
        val response = client!!.submitForm(
            url = "$baseUrl/admin/$path",
            formParameters = parameters { }
        ) {
            adminCookie()
        }
        return json.decodeFromString(response.bodyAsText())
    }

    private suspend fun postBody(path: String, body: String): ActionResult {
        val response = client!!.post("$baseUrl/admin/$path") {
            adminCookie()
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    // --- Playground chat (SSE) ---

    fun chatStream(
        model: String,
        messages: List<ChatMessage>,
        onChunk: (String) -> Unit,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        val c = client ?: return
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        scope.launch {
            try {
                val body = buildJsonObject {
                    put("model", model)
                    putJsonArray("messages") {
                        for (msg in messages) {
                            addJsonObject {
                                put("role", msg.role)
                                put("content", msg.content)
                            }
                        }
                    }
                    put("stream", true)
                }

                val response = c.submitForm(
                    url = "$baseUrl/admin/playground/chat",
                    formParameters = parameters {
                        append("model", model)
                        for (msg in messages) {
                            append("messages", "${msg.role}:${msg.content}")
                        }
                        append("stream", "true")
                    }
                ) {
                    adminCookie()
                }

                val reader = response.bodyAsChannel()
                val buffer = StringBuilder()
                while (true) {
                    val line = reader.readUTF8Line() ?: break
                    buffer.append(line)
                    if (line.isEmpty()) {
                        // Process buffered SSE event
                        val data = buffer.toString().trim()
                        buffer.clear()
                        if (data.startsWith("data: ")) {
                            val payload = data.removePrefix("data: ")
                            if (payload == "[DONE]") {
                                onDone()
                                return@launch
                            }
                            try {
                                val obj = json.parseToJsonElement(payload) as? JsonObject
                                val choices = obj?.get("choices") as? JsonArray
                                val delta = choices?.firstOrNull()?.jsonObject?.get("delta")?.jsonObject
                                val content = delta?.get("content")?.jsonPrimitive?.contentOrNull
                                if (content != null) {
                                    onChunk(content)
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }
                onDone()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    private val kotlinx.coroutines.CoroutineScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default)

    fun close() {
        client?.close()
        client = null
    }
}

data class ChatMessage(
    val role: String,
    val content: String
)
