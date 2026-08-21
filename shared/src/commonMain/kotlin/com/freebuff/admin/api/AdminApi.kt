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
import kotlinx.serialization.json.*

// ── Auth state ──

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data class Connected(val baseUrl: String) : ConnectionState()
}

// ── Data models matching actual freebuff-proxy JSON ──

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
    val has_tokens: Boolean = false
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
    val standing_score: Float = 0f,
    val standing_next_level: String = "",
    val standing_next_level_at: String = ""
)

@kotlinx.serialization.Serializable
data class TokensData(
    val mode: String = "",
    val in_bridge: Boolean = false,
    val bridge_tokens: Int = 0,
    val token_count: Int = 0,
    val tokens: List<TokenDetail> = emptyList(),
    val has_tokens: Boolean = false
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
    val quota: List<QuotaRow> = emptyList(),
    val has_quota: Boolean = false
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
    val has_bar: Boolean = false
)

@kotlinx.serialization.Serializable
data class ModelsData(
    val models: List<ModelRow> = emptyList(),
    val count: Int = 0,
    val agents: Int = 0,
    val aliases: List<AliasRow> = emptyList(),
    val has_aliases: Boolean = false
)

@kotlinx.serialization.Serializable
data class ModelRow(
    val id: String = "",
    val agent: String = ""
)

@kotlinx.serialization.Serializable
data class AliasRow(
    val alias: String = "",
    val real: String = ""
)

@kotlinx.serialization.Serializable
data class TracesData(
    val enabled: Boolean = false,
    val traces: List<TraceEntry> = emptyList()
)

@kotlinx.serialization.Serializable
data class TraceEntry(
    val time: String = "",
    val token: String = "",
    val model: String = "",
    val status: String = "",
    val ms: String = "",
    val error: String = "",
    val phases: List<PhaseKV> = emptyList()
)

@kotlinx.serialization.Serializable
data class PhaseKV(
    val key: String = "",
    val value: String = ""
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
    val has_tokens: Boolean = false
)

@kotlinx.serialization.Serializable
data class ConfigData(
    val env_content: String = "",
    val raw_content: String = "",
    val effective: Map<String, String> = emptyMap()
)

@kotlinx.serialization.Serializable
data class LogsData(
    val enabled: Boolean = false,
    val level: String = "",
    val msg: String = "",
    val has_filter: Boolean = false,
    val entries: List<LogEntry> = emptyList()
)

@kotlinx.serialization.Serializable
data class LogEntry(
    val time: String = "",
    val level: String = "",
    val message: String = "",
    val fields: String = ""
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
    val requests_trend: TrendInfo = TrendInfo(),
    val retries_trend: TrendInfo = TrendInfo(),
    val per_tokens: List<PerTokenMetrics> = emptyList()
)

@kotlinx.serialization.Serializable
data class TrendInfo(
    val direction: String = "flat",
    val percentage: Double = 0.0
)

@kotlinx.serialization.Serializable
data class PerTokenMetrics(
    val token: Int = 0,
    val requests_24h: Int = 0,
    val transient_retries: Long = 0,
    val fingerprint_rotations: Long = 0,
    val spend_day: Long = 0,
    val risk_level: String = ""
)

@kotlinx.serialization.Serializable
data class VersionData(
    val current_version: String = "",
    val has_update: Boolean = false,
    val latest_version: String = "",
    val update_url: String = ""
)

// ── API Client ──

class AdminApi {
    private var client: HttpClient? = null
    private var baseUrl: String = ""
    private var sessionCookie: String = ""

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    val connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)

    private fun ensureClient(): HttpClient {
        if (client == null) {
            client = HttpClient(OkHttp) {
                engine {
                    config {
                        followRedirects(false)
                    }
                }
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true })
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 30000
                    connectTimeoutMillis = 10000
                }
                expectSuccess = false
            }
        }
        return client!!
    }

    // ── Login ──

    suspend fun login(serverUrl: String, password: String): Boolean {
        baseUrl = serverUrl.trimEnd('/')
        val c = ensureClient()
        return try {
            val response = c.submitForm(
                url = "$baseUrl/admin/login",
                formParameters = parameters { append("token", password) }
            )
            // Server returns 302 with Set-Cookie on success, 401 on failure
            val setCookie = response.headers["Set-Cookie"]
            if (setCookie != null && setCookie.contains("fb_admin=")) {
                sessionCookie = setCookie.substringBefore(";")
                connectionState.value = ConnectionState.Connected(baseUrl)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            connectionState.value = ConnectionState.Disconnected
            false
        }
    }

    fun restoreSession(url: String, cookie: String) {
        baseUrl = url
        sessionCookie = cookie
        connectionState.value = ConnectionState.Connected(url)
    }

    fun logout() {
        sessionCookie = ""
        connectionState.value = ConnectionState.Disconnected
    }

    fun getSessionCookie(): String = sessionCookie

    private fun HttpRequestBuilder.adminCookie() {
        // Cookie jar handles this automatically via OkHttp engine
    }

    // ── GET ──

    private suspend inline fun <reified T> get(path: String): T {
        val c = ensureClient()
        val response: HttpResponse = c.get("$baseUrl$path") { adminCookie() }
        return json.decodeFromString(response.bodyAsText())
    }

    // ── POST ──

    private suspend fun postForm(path: String, formBuilder: ParametersBuilder.() -> Unit = {}): HttpResponse {
        val c = ensureClient()
        return c.submitForm(url = "$baseUrl$path", formParameters = Parameters.build(formBuilder)) { adminCookie() }
    }

    // ── API methods ──

    suspend fun getOverview(): OverviewData = get("/admin/api/overview")
    suspend fun getTokens(): TokensData = get("/admin/api/tokens")
    suspend fun getModels(): ModelsData = get("/admin/api/models")
    suspend fun getTraces(): TracesData = get("/admin/api/traces")
    suspend fun getSetup(): SetupData = get("/admin/api/setup")
    suspend fun getConfig(): ConfigData = get("/admin/api/config")
    suspend fun getLogs(level: String = "", msg: String = ""): LogsData {
        var path = "/admin/api/logs"
        val params = mutableListOf<String>()
        if (level.isNotEmpty()) params.add("level=$level")
        if (msg.isNotEmpty()) params.add("msg=$msg")
        if (params.isNotEmpty()) path += "?" + params.joinToString("&")
        return get(path)
    }
    suspend fun getMetrics(): MetricsData = get("/admin/api/metrics")
    suspend fun getVersion(): VersionData = get("/admin/api/version")

    suspend fun saveConfig(content: String): String {
        val resp = postForm("/admin/config") { append("content", content) }
        return resp.bodyAsText()
    }

    suspend fun reloadConfig(): String {
        val resp = postForm("/admin/reload")
        return resp.bodyAsText()
    }

    suspend fun testToken(id: Int): String {
        val resp = postForm("/admin/tokens/$id/test")
        return resp.bodyAsText()
    }

    suspend fun testAllTokens(): String {
        val resp = postForm("/admin/tokens/test-all")
        return resp.bodyAsText()
    }

    suspend fun unlockToken(id: Int): String {
        val resp = postForm("/admin/tokens/$id/unlock")
        return resp.bodyAsText()
    }

    suspend fun finishToken(id: Int): String {
        val resp = postForm("/admin/tokens/$id/finish")
        return resp.bodyAsText()
    }

    suspend fun addToken(): String {
        val resp = postForm("/admin/tokens/add")
        return resp.bodyAsText()
    }

    suspend fun removeToken(): String {
        val resp = postForm("/admin/tokens/remove")
        return resp.bodyAsText()
    }

    suspend fun switchMode(): String {
        val resp = postForm("/admin/mode")
        return resp.bodyAsText()
    }

    suspend fun smokeTest(model: String, prompt: String): String {
        val resp = postForm("/admin/smoke") {
            append("model", model)
            append("prompt", prompt)
        }
        return resp.bodyAsText()
    }

    suspend fun chat(model: String, messages: String): String {
        val resp = postForm("/admin/playground/chat") {
            append("model", model)
            append("messages", messages)
        }
        return resp.bodyAsText()
    }

    fun close() {
        client?.close()
        client = null
    }
}
