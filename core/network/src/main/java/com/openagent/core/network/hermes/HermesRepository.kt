package com.openagent.core.network.hermes

import com.openagent.core.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hermes Repository - 封装 REST API 通信
 *
 * 提供:
 * - 连接配置与测试
 * - 模型管理
 * - 聊天请求
 * - 会话与用量查询
 */
@Singleton
class HermesRepository @Inject constructor(
    private val apiClient: HermesApiClient
) {
    fun configure(url: String, apiKey: String, apiMode: ApiMode = ApiMode.CHAT_COMPLETIONS) {
        apiClient.configure(url, apiKey, apiMode)
    }

    suspend fun testConnection(): Result<String> = apiClient.testConnection()

    suspend fun getModels(): Result<List<ModelInfo>> = apiClient.getModels()

    suspend fun chat(prompt: String, model: String): Flow<String> {
        val request = ChatRequest(
            model = model,
            messages = listOf(ChatMessage(role = "user", content = prompt)),
            stream = true
        )
        return apiClient.chat(request)
    }

    suspend fun getSessions(): Result<List<SessionInfo>> = apiClient.getSessions()

    suspend fun getUsageStats(): Result<UsageStats> = apiClient.getUsageStats()
}
