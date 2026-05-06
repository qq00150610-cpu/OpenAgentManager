package com.openagent.manager.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openagent.core.model.*
import com.openagent.core.ui.components.SectionHeader
import com.openagent.feature.openclaw.ui.*
import com.openagent.feature.openclaw.viewmodel.OpenClawViewModel
import com.openagent.feature.openhermes.ui.*
import com.openagent.feature.openhermes.viewmodel.HermesViewModel

@Composable
fun DashboardScreen(
    openClawVm: OpenClawViewModel = hiltViewModel(),
    hermesVm: HermesViewModel = hiltViewModel()
) {
    val openClawState by openClawVm.uiState.collectAsState()
    val hermesState by hermesVm.uiState.collectAsState()

    var gatewayUrl by remember { mutableStateOf("") }
    var gatewayToken by remember { mutableStateOf("") }
    var hermesUrl by remember { mutableStateOf("") }
    var hermesApiKey by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Connection Cards
        SectionHeader("连接管理")

        OpenClawConnectionCard(
            connectionState = openClawState.connectionState,
            gatewayUrl = gatewayUrl,
            onUrlChange = { gatewayUrl = it },
            token = gatewayToken,
            onTokenChange = { gatewayToken = it },
            onConnect = { openClawVm.connect(gatewayUrl, gatewayToken) },
            onDisconnect = { openClawVm.disconnect() }
        )

        HermesConnectionCard(
            isConnected = hermesState.isConnected,
            isConnecting = hermesState.isConnecting,
            serverUrl = hermesUrl,
            onUrlChange = { hermesUrl = it },
            apiKey = hermesApiKey,
            onApiKeyChange = { hermesApiKey = it },
            apiMode = ApiMode.CHAT_COMPLETIONS,
            onApiModeChange = {},
            onConnect = {
                hermesVm.configure(hermesUrl, hermesApiKey)
                hermesVm.testConnection()
            },
            onDisconnect = { hermesVm.disconnect() }
        )

        // Stats
        SectionHeader("OpenClaw 统计")
        OpenClawStatsGrid(
            activeSessions = openClawState.activeSessions,
            messageThroughput = 0L,
            gatewayLatency = openClawState.gatewayLatency,
            connectedChannels = openClawState.channels.size
        )

        SectionHeader("Hermes 统计")
        HermesStatsCards(
            usageStats = hermesState.usageStats,
            activeSessions = hermesState.sessions.size
        )

        // Quick Actions
        SectionHeader("快捷操作")
        QuickActions(
            onStartAgent = {},
            onSwitchModel = {},
            onHealthCheck = {}
        )

        // Logs
        SectionHeader("实时日志")
        LogViewer(
            logs = openClawState.logs,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
