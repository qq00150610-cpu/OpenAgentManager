package com.openagent.core.model

import kotlinx.serialization.Serializable

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
