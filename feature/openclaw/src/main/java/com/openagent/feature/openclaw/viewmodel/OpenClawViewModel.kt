package com.openagent.feature.openclaw.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openagent.core.model.*
import com.openagent.core.network.openclaw.OpenClawRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * OpenClaw UI 状态
 */
data class OpenClawUiState(
    val connectionState: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val agents: List<AgentInfo> = emptyList(),
    val channels: List<ChannelInfo> = emptyList(),
    val logs: List<LogEntry> = emptyList(),
    val activeSessions: Int = 0,
    val gatewayLatency: Long = 0L,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OpenClawViewModel @Inject constructor(
    private val repository: OpenClawRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OpenClawUiState())
    val uiState: StateFlow<OpenClawUiState> = _uiState.asStateFlow()

    init {
        // 监听连接状态
        viewModelScope.launch {
            repository.connectionState.collect { state ->
                _uiState.update {
                    it.copy(
                        connectionState = state,
                        isConnecting = state == ConnectionStatus.CONNECTING
                    )
                }
            }
        }
        // 监听日志流（最多保留 500 条）
        viewModelScope.launch {
            repository.logs.collect { log ->
                _uiState.update {
                    it.copy(logs = (it.logs + log).takeLast(500))
                }
            }
        }
        // 监听事件流
        viewModelScope.launch {
            repository.events.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: WebSocketEvent) {
        when (event.event) {
            "agents.updated" -> refreshAgents()
            "channels.updated" -> refreshChannels()
        }
    }

    fun connect(url: String, token: String) {
        _uiState.update { it.copy(isConnecting = true, errorMessage = null) }
        repository.connect(url, token)
    }

    fun disconnect() {
        repository.disconnect()
        _uiState.update { it.copy(agents = emptyList(), channels = emptyList()) }
    }

    fun refreshAgents() {
        viewModelScope.launch { repository.getAgents() }
    }

    fun refreshChannels() {
        viewModelScope.launch { repository.getChannels() }
    }

    fun sendInstruction(agentId: String, instruction: String) {
        repository.sendInstruction(agentId, instruction)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
