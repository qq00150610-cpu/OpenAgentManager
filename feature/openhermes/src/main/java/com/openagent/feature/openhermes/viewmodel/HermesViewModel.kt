package com.openagent.feature.openhermes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openagent.core.model.*
import com.openagent.core.network.hermes.HermesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HermesUiState(
    val isConnected: Boolean = false,
    val models: List<ModelInfo> = emptyList(),
    val sessions: List<SessionInfo> = emptyList(),
    val usageStats: UsageStats = UsageStats(),
    val selectedModel: String = "",
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HermesViewModel @Inject constructor(
    private val repository: HermesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HermesUiState())
    val uiState: StateFlow<HermesUiState> = _uiState.asStateFlow()

    fun configure(url: String, apiKey: String, apiMode: ApiMode = ApiMode.CHAT_COMPLETIONS) {
        repository.configure(url, apiKey, apiMode)
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, errorMessage = null) }
            repository.testConnection().fold(
                onSuccess = {
                    _uiState.update { it.copy(isConnected = true, isConnecting = false) }
                    loadModels()
                    loadSessions()
                    loadUsageStats()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isConnecting = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            repository.getModels().fold(
                onSuccess = { m -> _uiState.update { it.copy(models = m) } },
                onFailure = { }
            )
        }
    }

    fun loadSessions() {
        viewModelScope.launch {
            repository.getSessions().fold(
                onSuccess = { s -> _uiState.update { it.copy(sessions = s) } },
                onFailure = { }
            )
        }
    }

    fun loadUsageStats() {
        viewModelScope.launch {
            repository.getUsageStats().fold(
                onSuccess = { u -> _uiState.update { it.copy(usageStats = u) } },
                onFailure = { }
            )
        }
    }

    fun selectModel(modelId: String) {
        _uiState.update { it.copy(selectedModel = modelId) }
    }

    fun disconnect() {
        _uiState.update { it.copy(isConnected = false, models = emptyList(), sessions = emptyList()) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
