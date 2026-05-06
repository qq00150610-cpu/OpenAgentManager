package com.openagent.core.network.hermes

import com.openagent.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hermes Agent REST API 客户端
 *
 * 支持三种 API 模式:
 * - chat_completions: OpenAI 兼容端点
 * - codex_responses: OpenAI Codex / Responses API
 * - anthropic_messages: Anthropic 原生 Messages API
 */
@Singleton
class HermesApiClient @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private var client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private var baseUrl = ""
    private var apiKey = ""
    private var apiMode = ApiMode.CHAT_COMPLETIONS

    fun configure(url: String, key: String, mode: ApiMode = ApiMode.CHAT_COMPLETIONS) {
        baseUrl = url.trimEnd('/')
        apiKey = key
        apiMode = mode
    }

    // ── 连接测试 ─────────────────────────────

    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/v1/models")
                .addHeader("Authorization", "Bearer $apiKey")
                .get().build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) Result.success("连接成功")
            else Result.failure(IOException("HTTP ${response.code}: ${response.message}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    // ── 模型列表 ─────────────────────────────

    suspend fun getModels(): Result<List<ModelInfo>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/v1/models")
                .addHeader("Authorization", "Bearer $apiKey")
                .get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (response.isSuccessful) {
                val models = json.parseToJsonElement(body).jsonObject["data"]
                    ?.jsonArray?.map { el ->
                        val obj = el.jsonObject
                        ModelInfo(
                            id = obj["id"]?.jsonPrimitive?.content ?: "",
                            name = obj["id"]?.jsonPrimitive?.content ?: "",
                            provider = obj["owned_by"]?.jsonPrimitive?.content ?: ""
                        )
                    } ?: emptyList()
                Result.success(models)
            } else Result.failure(IOException("HTTP ${response.code}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    // ── 聊天请求 (SSE 流式) ───────────────────

    suspend fun chat(request: ChatRequest): Flow<String> = callbackFlow {
        val url = when (apiMode) {
            ApiMode.CHAT_COMPLETIONS -> "$baseUrl/v1/chat/completions"
            ApiMode.CODEX_RESPONSES -> "$baseUrl/v1/responses"
            ApiMode.ANTHROPIC_MESSAGES -> "$baseUrl/v1/messages"
        }

        val body = when (apiMode) {
            ApiMode.CHAT_COMPLETIONS -> json.encodeToString(ChatRequest.serializer(), request)
            ApiMode.CODEX_RESPONSES -> buildJsonObject {
                put("model", request.model)
                put("input", request.messages.lastOrNull()?.content ?: "")
                put("stream", request.stream)
            }.toString()
            ApiMode.ANTHROPIC_MESSAGES -> buildJsonObject {
                put("model", request.model)
                put("max_tokens", 4096)
                putJsonArray("messages") {
                    addJsonObject {
                        put("role", "user")
                        put("content", request.messages.lastOrNull()?.content ?: "")
                    }
                }
            }.toString()
        }

        val httpRequest = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val factory = EventSources.createFactory(client)
        val eventSource = factory.newEventSource(httpRequest, object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                trySend(data)
            }
            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                if (response != null) {
                    trySend(response.body?.string() ?: "")
                }
                close(t ?: IOException("SSE connection failed"))
            }
            override fun onClosed(eventSource: EventSource) {
                close()
            }
        })
        awaitClose {
            eventSource.cancel()
        }
    }

    // ── 会话列表 ─────────────────────────────

    suspend fun getSessions(): Result<List<SessionInfo>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/v1/sessions")
                .addHeader("Authorization", "Bearer $apiKey")
                .get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (response.isSuccessful) {
                Result.success(json.decodeFromString<List<SessionInfo>>(body))
            } else Result.failure(IOException("HTTP ${response.code}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    // ── 用量统计 ─────────────────────────────

    suspend fun getUsageStats(): Result<UsageStats> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/v1/usage")
                .addHeader("Authorization", "Bearer $apiKey")
                .get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: "{}"
            if (response.isSuccessful) {
                Result.success(json.decodeFromString<UsageStats>(body))
            } else Result.failure(IOException("HTTP ${response.code}"))
        } catch (e: Exception) { Result.failure(e) }
    }
}
