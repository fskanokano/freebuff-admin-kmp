package com.freebuff.admin.model

import kotlinx.serialization.Serializable

@Serializable
data class OverviewData(
    val health: String = "",
    val uptime: String = "",
    val mode: String = "",
    val token_count: Int = 0,
    val bridge_tokens: Int = 0,
    val model_count: Int = 0,
    val requests_total: Long = 0,
    val requests_today: Long = 0,
    val messages_today: Long = 0,
    val tokens_active: Int = 0,
    val tokens_cooldown: Int = 0,
    val tokens_banned: Int = 0,
    val tokens_idle: Int = 0,
    val pending_runs: Int = 0,
    val queue_depth: Int = 0,
    val spend_today: String = "",
    val spend_limit: String = "",
    val last_used_proxy: String = "",
    val last_used_at: String = "",
    val has_update: Boolean = false,
    val latest_version: String = "",
    val recent_routes: List<RouteEntry> = emptyList(),
    val token_cards: List<TokenCard> = emptyList()
)

@Serializable
data class RouteEntry(
    val name: String = "",
    val status: String = "",
    val at: String = "",
    val model: String = "",
    val http: Int = 0,
    val attempts: Int = 0,
    val ms: Long = 0,
    val ok: Boolean = false
)

@Serializable
data class TokenCard(
    val token: Int = 0,
    val status: String = "",
    val session_status: String = "",
    val queue_position: Int = 0,
    val queue_depth: Int = 0,
    val active_runs: Int = 0,
    val requests: Long = 0,
    val messages_24h: Int = 0,
    val daily_limit: Int = 0,
    val usage_pct: Int = 0,
    val risk_level: String = "",
    val cooldown_active: Boolean = false,
    val cooldown_until: String = "",
    val has_standing: Boolean = false,
    val standing_level: String = "",
    val standing_label: String = "",
    val standing_score: Double = 0.0,
    val standing_next_level: String = "",
    val standing_next_level_at: String = "",
    val transient_retries: Long = 0
)

@Serializable
data class TokensData(
    val mode: String = "",
    val in_bridge: Boolean = false,
    val bridge_tokens: Int = 0,
    val token_count: Int = 0,
    val tokens: List<TokenDetail> = emptyList(),
    val has_tokens: Boolean = false
)

@Serializable
data class TokenDetail(
    val token: Int = 0,
    val status: String = "",
    val session_status: String = "",
    val queue_position: Int = 0,
    val queue_depth: Int = 0,
    val active_runs: Int = 0,
    val requests: Long = 0,
    val messages_24h: Int = 0,
    val daily_limit: Int = 0,
    val usage_pct: Int = 0,
    val risk_level: String = "",
    val cooldown_active: Boolean = false,
    val cooldown_until: String = "",
    val has_standing: Boolean = false,
    val standing_level: String = "",
    val standing_label: String = "",
    val standing_score: Double = 0.0,
    val session_instance: String = "",
    val quota: List<QuotaRow> = emptyList(),
    val has_quota: Boolean = false,
    val transient_retries: Long = 0
)

@Serializable
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

@Serializable
data class ModelsData(
    val models: List<ModelRow> = emptyList(),
    val count: Int = 0,
    val agents: Int = 0,
    val aliases: List<AliasRow> = emptyList(),
    val has_aliases: Boolean = false
)

@Serializable
data class ModelRow(
    val id: String = "",
    val agent: String = ""
)

@Serializable
data class AliasRow(
    val alias: String = "",
    val real: String = ""
)

@Serializable
data class TracesData(
    val enabled: Boolean = false,
    val traces: List<TraceEntry> = emptyList()
)

@Serializable
data class TraceEntry(
    val time: String = "",
    val token: String = "",
    val model: String = "",
    val status: String = "",
    val ms: String = "",
    val error: String = "",
    val phases: List<PhaseKV> = emptyList()
)

@Serializable
data class PhaseKV(
    val key: String = "",
    val value: String = ""
)

@Serializable
data class LogsData(
    val enabled: Boolean = false,
    val level: String = "",
    val msg: String = "",
    val has_filter: Boolean = false,
    val entries: List<LogEntry> = emptyList()
)

@Serializable
data class LogEntry(
    val time: String = "",
    val level: String = "",
    val message: String = "",
    val fields: String = ""
)

@Serializable
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
    val per_tokens: List<PerTokenMetrics> = emptyList()
)

@Serializable
data class MetricTrend(
    val direction: String = "",
    val percentage: Double = 0.0
)

@Serializable
data class PerTokenMetrics(
    val token: Int = 0,
    val requests_24h: Int = 0,
    val transient_retries: Long = 0,
    val fingerprint_rotations: Long = 0,
    val spend_day: Long = 0,
    val risk_level: String = ""
)

@Serializable
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

@Serializable
data class ConfigData(
    val listen_addr: String = "",
    val upstream_base_url: String = "",
    val auth_tokens: List<String> = emptyList(),
    val auth_tokens_set: Boolean = false,
    val api_keys: List<String> = emptyList(),
    val admin_token: String = "",
    val safe_mode: Boolean = false,
    val tls_fingerprint: String = "",
    val rotation_interval: String = "",
    val request_timeout: String = "",
    val session_call_timeout: String = "",
    val max_messages_per_day: Int = 0,
    val bridge_daily_limit: Int = 0,
    val max_spend_per_day: Long = 0,
    val idle_rotation_timeout: String = "",
    val models_hide_unavailable: Boolean = false,
    val models_allow: List<String> = emptyList(),
    val cors_allowed_origin: String = "",
    val request_jitter: String = "",
    val cli_version: String = "",
    val model_aliases: Map<String, String> = emptyMap(),
    val transient_retries: Int = 0,
    val session_persist: Boolean = false,
    val session_state_file: String = "",
    val rate_limit_per_ip: Double = 0.0,
    val rate_limit_burst: Int = 0,
    val prefer_max_models: Boolean = false,
    val dashboard_enabled: Boolean = true,
    val log_access: Boolean = true,
    val log_level: String = "",
    val log_format: String = "",
    val log_ring_size: Int = 500,
    val webhook_url: String = "",
    val fallback_after: String = "",
    val fallback_models: Map<String, String> = emptyMap(),
    val http2_upstream: Boolean = false,
    val acting_user_id: String = ""
)

@Serializable
data class VersionData(
    val current_version: String = "",
    val has_update: Boolean = false,
    val latest_version: String = "",
    val update_url: String = ""
)

@Serializable
data class ActionResponse(
    val ok: Boolean = false,
    val message: String = ""
)

@Serializable
data class LoginResponse(
    val ok: Boolean = false,
    val error: String = "",
    val message: String = ""
)
