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
//  免费 API 提供商模型
// ═══════════════════════════════════════════

/**
 * 预设的免费 AI API 提供商
 */
@Serializable
data class FreeApiProvider(
    val id: String,
    val name: String,
    val baseUrl: String,
    val apiKeyHint: String,
    val description: String,
    val models: List<String> = emptyList(),
    val isFree: Boolean = true,
    val officialSite: String = ""
)

/**
 * 精选的 10 个免费 AI API 提供商预设
 * 这些提供商大部分支持 OpenAI 兼容格式，可直接用于 Hermes 的 chat_completions 模式
 */
val FREE_API_PROVIDERS = listOf(
    FreeApiProvider(
        id = "deepseek",
        name = "DeepSeek",
        baseUrl = "https://api.deepseek.com",
        apiKeyHint = "从 deepseek.com 获取 API Key",
        description = "深度求索 DeepSeek-V3/DeepSeek-R1，性能强劲，价格极低，注册送额度",
        models = listOf("deepseek-chat", "deepseek-reasoner"),
        officialSite = "https://platform.deepseek.com"
    ),
    FreeApiProvider(
        id = "openrouter",
        name = "OpenRouter",
        baseUrl = "https://openrouter.ai/api/v1",
        apiKeyHint = "从 openrouter.ai 获取 API Key",
        description = "统一网关，免费模型含 Llama 3、Mistral、Gemma 等，注册送 $1 额度",
        models = listOf("meta-llama/llama-3-8b-instruct", "mistralai/mistral-7b-instruct", "google/gemma-2-9b-it"),
        officialSite = "https://openrouter.ai"
    ),
    FreeApiProvider(
        id = "groq",
        name = "Groq",
        baseUrl = "https://api.groq.com/openai/v1",
        apiKeyHint = "从 groq.com 获取免费 API Key",
        description = "超低延迟推理，免费使用 Llama 3、Mixtral、Gemma 等模型，速率限制 30 RPM",
        models = listOf("llama3-8b-8192", "llama3-70b-8192", "mixtral-8x7b-32768"),
        officialSite = "https://console.groq.com"
    ),
    FreeApiProvider(
        id = "together",
        name = "Together AI",
        baseUrl = "https://api.together.xyz/v1",
        apiKeyHint = "从 together.ai 获取 API Key",
        description = "开源模型集合，免费额度 $25，支持 Llama、Mistral、DeepSeek 等上百模型",
        models = listOf("mistralai/Mixtral-8x22B-Instruct-v0.1", "meta-llama/Llama-3-70b-chat-hf"),
        officialSite = "https://api.together.xyz"
    ),
    FreeApiProvider(
        id = "fireworks",
        name = "Fireworks AI",
        baseUrl = "https://api.fireworks.ai/inference/v1",
        apiKeyHint = "从 fireworks.ai 获取 API Key",
        description = "快速推理平台，免费额度包含 Llama 3、Mixtral、DeepSeek 等",
        models = listOf("accounts/fireworks/models/llama-v3-70b-instruct", "accounts/fireworks/models/deepseek-v3"),
        officialSite = "https://fireworks.ai"
    ),
    FreeApiProvider(
        id = "siliconflow",
        name = "SiliconFlow (硅基流动)",
        baseUrl = "https://api.siliconflow.cn/v1",
        apiKeyHint = "从 siliconflow.cn 获取 API Key",
        description = "国内稳定访问，免费使用 Qwen2、DeepSeek、GLM 等模型，注册送额度",
        models = listOf("Qwen/Qwen2-72B-Instruct", "deepseek-ai/DeepSeek-V2.5", "THUDM/glm-4-9b-chat"),
        officialSite = "https://cloud.siliconflow.cn"
    ),
    FreeApiProvider(
        id = "moonshot",
        name = "Moonshot (月之暗面)",
        baseUrl = "https://api.moonshot.cn/v1",
        apiKeyHint = "从 moonshot.cn 获取 API Key",
        description = "Kimi 大模型 API，支持长文本上下文，注册送 15 元额度",
        models = listOf("moonshot-v1-8k", "moonshot-v1-32k", "moonshot-v1-128k"),
        officialSite = "https://platform.moonshot.cn"
    ),
    FreeApiProvider(
        id = "zhipu",
        name = "智谱 AI (Zhipu)",
        baseUrl = "https://open.bigmodel.cn/api/paas/v4",
        apiKeyHint = "从 bigmodel.cn 获取 API Key",
        description = "GLM 系列模型，注册送 500 万 tokens，支持多模态",
        models = listOf("glm-4-plus", "glm-4-air", "glm-4-flash"),
        officialSite = "https://open.bigmodel.cn"
    ),
    FreeApiProvider(
        id = "gitee",
        name = "Gitee AI (码云)",
        baseUrl = "https://ai.gitee.com/v1",
        apiKeyHint = "从 ai.gitee.com 获取 API Key",
        description = "国内开源模型平台，免费使用 Qwen、DeepSeek、Yi 等模型",
        models = listOf("Qwen/Qwen2-72B-Instruct", "deepseek-ai/DeepSeek-V3"),
        officialSite = "https://ai.gitee.com"
    ),
    FreeApiProvider(
        id = "lepton",
        name = "Lepton AI",
        baseUrl = "https://api.lepton.ai/v1",
        apiKeyHint = "从 lepton.ai 获取 API Key",
        description = "高性能推理平台，注册送免费额度，支持 Llama、Mistral 等开源模型",
        models = listOf("llama3-70b", "mixtral-8x22b"),
        officialSite = "https://lepton.ai"
    )
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
