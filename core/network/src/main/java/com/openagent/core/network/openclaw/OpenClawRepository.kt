package com.openagent.core.network.openclaw

import com.openagent.core.model.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OpenClaw Repository - 封装 Gateway WebSocket 通信
 *
 * 提供:
 * - 连接管理 (connect/disconnect)
 * - Agent 指令发送
 * - Agent/Channel/Node 列表查询
 * - 实时事件与日志流
 */
@Singleton
class OpenClawRepository @Inject constructor(
    private val wsManager: OpenClawWebSocketManager
) {
    val connectionState: StateFlow<ConnectionStatus> = wsManager.connectionState
    val events: SharedFlow<WebSocketEvent> = wsManager.events
    val logs: SharedFlow<LogEntry> = wsManager.logs

    fun connect(gatewayUrl: String, token: String) {
        wsManager.connect(gatewayUrl, token)
    }

    fun disconnect() {
        wsManager.disconnect()
    }

    fun sendInstruction(agentId: String, instruction: String): String {
        val params = buildJsonObject {
            put("agentId", agentId)
            put("instruction", instruction)
        }
        return wsManager.send("agent.instruct", params)
    }

    fun getAgents(): String = wsManager.send("agents.list")

    fun getChannels(): String = wsManager.send("channels.list")

    fun getNodes(): String = wsManager.send("nodes.list")

    fun observeEvents(): SharedFlow<WebSocketEvent> = events

    fun observeLogs(): SharedFlow<LogEntry> = logs
}
