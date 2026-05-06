package com.openagent.core.network.openclaw

import com.openagent.core.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okhttp3.*
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class ConnectRequest(
    val type: String = "req",
    val id: String = UUID.randomUUID().toString(),
    val method: String = "connect",
    val params: ConnectParams
)

@Serializable
data class ConnectParams(
    val minProtocol: Int = 3,
    val maxProtocol: Int = 3,
    val client: ClientInfo,
    val role: String = "operator",
    val scopes: List<String> = listOf("operator.read", "operator.write"),
    val caps: List<String> = emptyList(),
    val commands: List<String> = emptyList(),
    val permissions: Map<String, Boolean> = emptyMap(),
    val auth: AuthInfo,
    val locale: String = "zh-CN",
    val userAgent: String = "openagent-manager/1.0.0",
    val device: DeviceInfo
)

@Serializable
data class ClientInfo(
    val id: String = "android-manager",
    val version: String = "1.0.0",
    val platform: String = "android",
    val mode: String = "operator"
)

@Serializable
data class AuthInfo(val token: String)

@Serializable
data class DeviceInfo(
    val id: String,
    val publicKey: String = "",
    val signature: String = "",
    val signedAt: Long = System.currentTimeMillis(),
    val nonce: String = UUID.randomUUID().toString()
)

@Serializable
data class FrameRequest(
    val type: String = "req",
    val id: String = UUID.randomUUID().toString(),
    val method: String,
    val params: JsonObject? = null
)

@Singleton
class OpenClawWebSocketManager @Inject constructor() {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionState: StateFlow<ConnectionStatus> = _connectionState.asStateFlow()

    private val _events = MutableSharedFlow<WebSocketEvent>(
        replay = 1,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<WebSocketEvent> = _events.asSharedFlow()

    private val _logs = MutableSharedFlow<LogEntry>(
        replay = 50,
        extraBufferCapacity = 200,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val logs: SharedFlow<LogEntry> = _logs.asSharedFlow()

    private var reconnectAttempt = 0
    private var gatewayUrl = ""
    private var authToken = ""

    fun connect(url: String, token: String) {
        gatewayUrl = url
        authToken = token
        reconnectAttempt = 0
        _connectionState.value = ConnectionStatus.CONNECTING
        doConnect()
    }

    private fun doConnect() {
        val wsUrl = gatewayUrl.replace("http://", "ws://").replace("https://", "wss://")
        val fullUrl = if (wsUrl.endsWith("/gateway")) wsUrl else "$wsUrl/gateway"

        val request = Request.Builder().url(fullUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                sendConnectRequest(webSocket)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch { handleMessage(text) }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                _connectionState.value = ConnectionStatus.DISCONNECTED
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _connectionState.value = ConnectionStatus.ERROR
                emitLog(LogLevel.ERROR, "WebSocket", "连接失败: ${t.message}")
                scheduleReconnect()
            }
        })
    }

    private fun sendConnectRequest(ws: WebSocket) {
        val connectReq = ConnectRequest(
            params = ConnectParams(
                auth = AuthInfo(token = authToken),
                device = DeviceInfo(
                    id = UUID.randomUUID().toString(),
                    publicKey = "",
                    signature = ""
                )
            )
        )
        ws.send(json.encodeToString(connectReq))
    }

    private suspend fun handleMessage(text: String) {
        try {
            val jsonObj = json.parseToJsonElement(text).jsonObject
            val type = jsonObj["type"]?.jsonPrimitive?.content

            when (type) {
                "res" -> {
                    val ok = jsonObj["ok"]?.jsonPrimitive?.booleanOrNull
                    if (ok == true) {
                        _connectionState.value = ConnectionStatus.CONNECTED
                        reconnectAttempt = 0
                        emitLog(LogLevel.INFO, "WebSocket", "连接成功")
                    } else {
                        val error = jsonObj["error"]?.jsonObject
                        emitLog(LogLevel.ERROR, "WebSocket", "错误: $error")
                    }
                }
                "event" -> {
                    val event = jsonObj["event"]?.jsonPrimitive?.content ?: ""
                    val payload = jsonObj["payload"]?.toString() ?: ""
                    val seq = jsonObj["seq"]?.jsonPrimitive?.longOrNull
                    _events.emit(WebSocketEvent("event", event, payload, seq))
                    emitLog(LogLevel.INFO, "Event", event)
                }
            }
        } catch (e: Exception) {
            emitLog(LogLevel.ERROR, "Parser", "解析消息失败: ${e.message}")
        }
    }

    private fun scheduleReconnect() {
        scope.launch {
            val delayMs = (1000L * (1 shl reconnectAttempt.coerceAtMost(5))).coerceAtMost(30_000L)
            reconnectAttempt++
            emitLog(LogLevel.INFO, "Reconnect", "将在 ${delayMs}ms 后重连 (第${reconnectAttempt}次)")
            delay(delayMs)
            if (_connectionState.value != ConnectionStatus.CONNECTED) {
                _connectionState.value = ConnectionStatus.CONNECTING
                doConnect()
            }
        }
    }

    fun send(method: String, params: JsonObject? = null): String {
        val id = UUID.randomUUID().toString()
        val frame = FrameRequest(id = id, method = method, params = params)
        webSocket?.send(json.encodeToString(frame))
        return id
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnect")
        _connectionState.value = ConnectionStatus.DISCONNECTED
    }

    private suspend fun emitLog(level: LogLevel, tag: String, message: String) {
        _logs.emit(LogEntry(level = level, tag = tag, message = message))
    }
}
