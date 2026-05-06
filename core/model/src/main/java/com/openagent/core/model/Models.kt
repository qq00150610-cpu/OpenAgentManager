package com.openagent.core.model

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════
//  统一 Agent 模型
// ═══════════════════════════════════════════

@Serializable
enum class AgentType {
    OPENCLAW, OPENHERMES
}

@Serializable
enum class AgentStatus {
    ONLINE, OFFLINE, BUSY, ERROR
}

@Serializable
data class AgentInfo(
    val id: String,
    val name: String,
    val type: AgentType,
    val status: AgentStatus = AgentStatus.OFFLINE,
    val activeSessions: Int = 0,
    val capabilities: List<String> = emptyList(),
    val lastHeartbeat: Long = 0L
)

// ═══════════════════════════════════════════
//  OpenClaw 专用模型
// ═══════════════════════════════════════════

@Serializable
data class GatewayConnection(
    val url: String,
    val token: String = "",
    val protocolVersion: Int = 3,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED
)

@Serializable
enum class ConnectionStatus {
    CONNECTED, CONNECTING, DISCONNECTED, ERROR
}

@Serializable
data class DeviceCapability(
    val camera: Boolean = false,
    val location: Boolean = false,
    val sms: Boolean = false,
    val callLog: Boolean = false,
    val notifications: Boolean = false
)

@Serializable
data class ChannelInfo(
    val id: String,
    val type: String,
    val status: String,
    val config: Map<String, String> = emptyMap()
)

@Serializable
data class WebSocketEvent(
    val type: String,
    val event: String,
    val payload: String = "",
    val seq: Long? = null,
    val stateVersion: Long? = null
)

// ═══════════════════════════════════════════
//  Hermes 专用模型
// ═══════════════════════════════════════════

@Serializable
enum class ApiMode {
    CHAT_COMPLETIONS, CODEX_RESPONSES, ANTHROPIC_MESSAGES
}

@Serializable
data class ModelProvider(
    val name: String,
    val apiMode: ApiMode = ApiMode.CHAT_COMPLETIONS,
    val baseUrl: String = "",
    val authType: String = "bearer"
)

@Serializable
data class SessionInfo(
    val id: String,
    val platform: String = "",
    val tokenCount: Long = 0L,
    val modelName: String = "",
    val status: String = "active"
)

@Serializable
data class ChannelConfig(
    val platform: String,
    val credentials: Map<String, String> = emptyMap(),
    val enabled: Boolean = false
)

@Serializable
data class UsageStats(
    val totalTokens: Long = 0L,
    val totalCost: Double = 0.0,
    val requestCount: Long = 0L,
    val modelBreakdown: Map<String, Long> = emptyMap()
)

@Serializable
data class ModelInfo(
    val id: String,
    val name: String,
    val provider: String = "",
    val contextWindow: Int = 0,
    val maxOutput: Int = 0
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean = true
)

// ═══════════════════════════════════════════
//  Agent 编辑器模型
// ═══════════════════════════════════════════

@Serializable
data class ScriptDocument(
    val id: String,
    val name: String,
    val content: String = "",
    val language: ScriptLanguage = ScriptLanguage.PYTHON,
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class ScriptLanguage(val displayName: String, val extension: String) {
    PYTHON("Python", "py"),
    YAML("YAML", "yaml"),
    MARKDOWN("Markdown", "md")
}

@Serializable
data class ExecutionResult(
    val scriptId: String,
    val output: String = "",
    val exitCode: Int = -1,
    val duration: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AgentTemplate(
    val id: String,
    val name: String,
    val description: String = "",
    val category: TemplateCategory = TemplateCategory.GENERAL,
    val codeSnippet: String = ""
)

@Serializable
enum class TemplateCategory(val displayName: String) {
    OPENCLAW_SKILL("OpenClaw"),
    HERMES_AGENT("Hermes"),
    PROMPT_TEMPLATE("提示词"),
    GENERAL("通用")
}

@Serializable
data class PromptVariable(
    val name: String,
    val placeholder: String = "",
    val defaultValue: String = ""
)

// ═══════════════════════════════════════════
//  日志模型
// ═══════════════════════════════════════════

@Serializable
data class LogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel = LogLevel.INFO,
    val tag: String = "",
    val message: String
)

@Serializable
enum class LogLevel {
    INFO, WARN, ERROR, DEBUG
}
