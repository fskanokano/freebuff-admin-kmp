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

// ── Data models (matching freebuff-proxy /admin/api/* JSON) ──

@kotlinx.serialization.Serializable
data class OverviewData(
    val healthy: Boolean = false,
    val uptime: String = "",
    val mode: String = "",
    val version: String = "",
    val started_at: String = "",
    val total_requests: Long = 0,
    val total_retries: Long = 0,
    val total_errors: Long = 0,
    val requests_today: Long = 0,
    val error_rate: Double = 0.0,
    val avg_latency_ms: Double = 0.0,
    val p99_latency_ms: Double = 0.0,
    val p95_latency_ms: Double = 0.0,
    val active_tokens: Int = 0,
    val total_tokens: Int = 0,
    val current_load: Int = 0,
    val max_load: Int = 0,
    val models: List<String> = emptyList(),
    val recent_routes: List<RouteEntry> = emptyList(),
    val recent_events: List<EventEntry> = emptyList(),
    val services: List<ServiceStatus> = emptyList(),
    val token_status: List<TokenStatus> = emptyList()
)

@kotlinx.serialization.Serializable
data class RouteEntry(
    val model: String = "",
    val target_model: String = "",
    val provider: String = "",
    val status: String = "",
    val latency_ms: Long = 0,
    val timestamp: String = "",
    val token_key_hint: String = "",
    val http_status: Int = 0,
    val error_code: String = "",
    val stage: String = ""
)

@kotlinx.serialization.Serializable
data class EventEntry(
    val time: String = "",
    val type: String = "",
    val detail: String = ""
)

@kotlinx.serialization.Serializable
data class ServiceStatus(
    val name: String = "",
    val healthy: Boolean = false,
    val latency_ms: Long = 0,
    val message: String = ""
)

@kotlinx.serialization.Serializable
data class TokenStatus(
    val key_hint: String = "",
    val state: String = "",
    val cooldown_until: String = ""
)

@kotlinx.serialization.Serializable
data class TokenDetail(
    val key_hint: String = "",
    val pool_id: String = "",
    val state: String = "",
    val in_flight: Int = 0,
    val requests: Long = 0,
    val errors: Long = 0,
    val successes: Long = 0,
    val failovers: Int = 0,
    val rate_limited: Int = 0,
    val credit_exhausted: Int = 0,
    val bad_response: Int = 0,
    val auth_rejected: Int = 0,
    val account_limited: Int = 0,
    val quota_hard: Int = 0,
    val country_blocked: Int = 0,
    val http_errors: Int = 0,
    val tls_errors: Int = 0,
    val dns_errors: Int = 0,
    val dial_timeout: Int = 0,
    val conn_reset: Int = 0,
    val cooldown_remaining: String = "",
    val total_runs: Int = 0,
    val active_runs: Int = 0,
    val total_errors: Int = 0,
    val model_stats: Map<String, ModelStats> = emptyMap()
)

@kotlinx.serialization.Serializable
data class ModelStats(
    val requests: Long = 0,
    val errors: Long = 0,
    val total_latency_ms: Long = 0,
    val last_used: String = ""
)

@kotlinx.serialization.Serializable
data class TokensData(
    val total: Int = 0,
    val active: Int = 0,
    val idle: Int = 0,
    val pinned: Int = 0,
    val warmed: Int = 0,
    val hot: Int = 0,
    val degraded: Int = 0,
    val expired: Int = 0,
    val total_in_flight: Int = 0,
    val max_in_flight: Int = 0,
    val tokens: List<TokenDetail> = emptyList()
)

@kotlinx.serialization.Serializable
data class ModelsData(
    val models: List<String> = emptyList(),
    val aliases: Map<String, String> = emptyMap(),
    val agent_models: Map<String, String> = emptyMap(),
    val supported_models: List<String> = emptyList()
)

@kotlinx.serialization.Serializable
data class TracesData(
    val traces: List<TraceEntry> = emptyList()
)

@kotlinx.serialization.Serializable
data class TraceEntry(
    val id: String = "",
    val model: String = "",
    val provider: String = "",
    val status: String = "",
    val latency_ms: Long = 0,
    val timestamp: String = "",
    val stages: List<StageEntry> = emptyList(),
    val request: TraceRequest? = null,
    val response: TraceResponse? = null
)

@kotlinx.serialization.Serializable
data class StageEntry(
    val name: String = "",
    val status: String = "",
    val latency_ms: Long = 0
)

@kotlinx.serialization.Serializable
data class TraceRequest(
    val model: String = "",
    val messages: Int = 0,
    val max_tokens: Int = 0,
    val stream: Boolean = false
)

@kotlinx.serialization.Serializable
data class TraceResponse(
    val status: Int = 0,
    val model: String = "",
    val provider: String = "",
    val usage: Map<String, Int> = emptyMap()
)

@kotlinx.serialization.Serializable
data class SetupData(
    val server_url: String = "",
    val client_configs: Map<String, String> = emptyMap()
)

@kotlinx.serialization.Serializable
data class ConfigData(
    val env_content: String = "",
    val raw_content: String = "",
    val effective: Map<String, String> = emptyMap()
)

@kotlinx.serialization.Serializable
data class LogsData(
    val entries: List<LogEntry> = emptyList()
)

@kotlinx.serialization.Serializable
data class LogEntry(
    val time: String = "",
    val level: String = "",
    val msg: String = "",
    val request_id: String = ""
)

@kotlinx.serialization.Serializable
data class MetricsData(
    val uptime_seconds: Long = 0,
    val total_requests: Long = 0,
    val total_tokens_processed: Long = 0,
    val total_latency_ms: Long = 0,
    val requests_per_minute: Double = 0.0,
    val error_rate: Double = 0.0,
    val avg_latency_ms: Double = 0.0,
    val p50_latency_ms: Double = 0.0,
    val p90_latency_ms: Double = 0.0,
    val p99_latency_ms: Double = 0.0,
    val total_retries: Long = 0,
    val retry_rate: Double = 0.0,
    val rate_limited_count: Long = 0,
    val credit_exhausted_count: Long = 0,
    val tls_error_count: Long = 0,
    val dns_error_count: Long = 0,
    val connection_error_count: Long = 0,
    val trends: Map<String, TrendEntry> = emptyMap(),
    val model_stats: Map<String, ModelMetric> = emptyMap(),
    val token_stats: List<TokenMetric> = emptyList(),
    val routes: List<RouteMetric> = emptyList()
)

@kotlinx.serialization.Serializable
data class TrendEntry(
    val current: Double = 0.0,
    val previous: Double = 0.0,
    val change_pct: Double = 0.0,
    val direction: String = ""
)

@kotlinx.serialization.Serializable
data class ModelMetric(
    val requests: Long = 0,
    val errors: Long = 0,
    val avg_latency_ms: Double = 0.0,
    val total_tokens: Long = 0
)

@kotlinx.serialization.Serializable
data class TokenMetric(
    val key_hint: String = "",
    val state: String = "",
    val requests: Long = 0,
    val errors: Long = 0,
    val in_flight: Int = 0
)

@kotlinx.serialization.Serializable
data class RouteMetric(
    val model: String = "",
    val provider: String = "",
    val requests: Long = 0,
    val errors: Long = 0,
    val avg_latency_ms: Double = 0.0
)

@kotlinx.serialization.Serializable
data class VersionData(
    val version: String = "",
    val build_time: String = "",
    val go_version: String = ""
)

// ── API Client ──

class AdminApi {

    private var client: HttpClient? = null
    private var baseUrl: String = ""
    private var adminToken: String = ""
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
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        coerceInputValues = true
                    })
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

    suspend fun login(host: String, port: Int, password: String): Boolean {
        baseUrl = "http://$host:$port"
        adminToken = password
        val c = ensureClient()
        return try {
            val response = c.submitForm(
                url = "$baseUrl/admin/login",
                formParameters = parameters {
                    append("token", password)
                }
            )
            val setCookie = response.headers["Set-Cookie"]
            if (setCookie != null && response.status == HttpStatusCode.OK) {
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

    fun logout() {
        sessionCookie = ""
        connectionState.value = ConnectionState.Disconnected
    }

    private fun HttpRequestBuilder.adminCookie() {
        header("Cookie", sessionCookie)
    }

    // ── GET helpers ──

    private suspend inline fun <reified T> get(path: String): T {
        val c = ensureClient()
        val response: HttpResponse = c.get("$baseUrl$path") {
            adminCookie()
        }
        return json.decodeFromString(response.bodyAsText())
    }

    private suspend inline fun postRaw(path: String, formBuilder: ParametersBuilder.() -> Unit = {}): HttpResponse {
        val c = ensureClient()
        return c.submitForm(
            url = "$baseUrl$path",
            formParameters = Parameters.build(formBuilder)
        ) {
            adminCookie()
        }
    }

    // ── API methods ──

    suspend fun getOverview(): OverviewData = get("/admin/api/overview")

    suspend fun getTokens(): TokensData = get("/admin/api/tokens")

    suspend fun getModels(): ModelsData = get("/admin/api/models")

    suspend fun getTraces(): TracesData = get("/admin/api/traces")

    suspend fun getSetup(): SetupData = get("/admin/api/setup")

    suspend fun getConfig(): ConfigData = get("/admin/api/config")

    suspend fun getLogs(): LogsData = get("/admin/api/logs")

    suspend fun getMetrics(): MetricsData = get("/admin/api/metrics")

    suspend fun getVersion(): VersionData = get("/admin/api/version")

    // ── POST actions ──

    suspend fun saveConfig(content: String): Boolean {
        val resp = postRaw("/admin/config") {
            append("content", content)
        }
        return resp.status == HttpStatusCode.OK
    }

    suspend fun reloadConfig(): Boolean {
        val resp = postRaw("/admin/reload")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun testToken(id: String): Boolean {
        val resp = postRaw("/admin/tokens/$id/test")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun testAllTokens(): Boolean {
        val resp = postRaw("/admin/tokens/test-all")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun unlockToken(id: String): Boolean {
        val resp = postRaw("/admin/tokens/$id/unlock")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun finishToken(id: String): Boolean {
        val resp = postRaw("/admin/tokens/$id/finish")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun addToken(): Boolean {
        val resp = postRaw("/admin/tokens/add")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun removeToken(): Boolean {
        val resp = postRaw("/admin/tokens/remove")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun switchMode(): Boolean {
        val resp = postRaw("/admin/mode")
        return resp.status == HttpStatusCode.OK
    }

    suspend fun runDiagnostics(): JsonObject {
        val c = ensureClient()
        val resp: HttpResponse = postRaw("/admin/diag")
        return json.parseToJsonElement(resp.bodyAsText()) as JsonObject
    }

    suspend fun smokeTest(model: String, prompt: String, stream: Boolean): JsonObject {
        val c = ensureClient()
        val resp = postRaw("/admin/smoke") {
            append("model", model)
            append("prompt", prompt)
            append("stream", stream.toString())
        }
        return json.parseToJsonElement(resp.bodyAsText()) as JsonObject
    }

    // ── Playground (non-streaming) ──

    suspend fun chat(model: String, messages: List<ChatMessage>): String {
        val c = ensureClient()
        val resp = postRaw("/admin/playground/chat") {
            append("model", model)
            for (msg in messages) {
                append("messages", "${msg.role}:${msg.content}")
            }
        }
        return resp.bodyAsText()
    }

    fun close() {
        client?.close()
        client = null
    }
}

data class ChatMessage(
    val role: String,
    val content: String
)
