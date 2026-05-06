package com.openagent.core.model

import kotlinx.serialization.Serializable

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
