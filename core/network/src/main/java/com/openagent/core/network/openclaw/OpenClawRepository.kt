package com.openagent.core.network.openclaw

import com.openagent.core.model.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

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

    fun getAgents(): String {
        return wsManager.send("agents.list")
    }

    fun getChannels(): String {
        return wsManager.send("channels.list")
    }

    fun getNodes(): String {
        return wsManager.send("nodes.list")
    }

    fun observeEvents(): SharedFlow<WebSocketEvent> = events
    fun observeLogs(): SharedFlow<LogEntry> = logs
}
