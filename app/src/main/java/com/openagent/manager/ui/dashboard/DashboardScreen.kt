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

/**
 * 仪表盘 - 主页
 *
 * 布局结构（从上到下）:
 * 1. OpenClaw 连接卡片
 * 2. Hermes 连接卡片
 * 3. OpenClaw 统计网格 (2x2)
 * 4. Hermes 统计卡片 (1x3)
 * 5. 快捷操作栏
 * 6. 实时日志查看器
 */
@Composable
fun DashboardScreen(
    openClawVm: OpenClawViewModel = hiltViewModel(),
    hermesVm: HermesViewModel = hiltViewModel()
) {
    val ocState by openClawVm.uiState.collectAsState()
    val hmState by hermesVm.uiState.collectAsState()

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
        // ── 连接管理 ─────────────────────
        SectionHeader("连接管理")

        OpenClawConnectionCard(
            connectionState = ocState.connectionState,
            gatewayUrl = gatewayUrl,
            onUrlChange = { gatewayUrl = it },
            token = gatewayToken,
            onTokenChange = { gatewayToken = it },
            onConnect = { openClawVm.connect(gatewayUrl, gatewayToken) },
            onDisconnect = { openClawVm.disconnect() }
        )

        HermesConnectionCard(
            isConnected = hmState.isConnected,
            isConnecting = hmState.isConnecting,
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

        // ── OpenClaw 统计 ────────────────
        SectionHeader("OpenClaw 统计")
        OpenClawStatsGrid(
            activeSessions = ocState.activeSessions,
            messageThroughput = 0L,
            gatewayLatency = ocState.gatewayLatency,
            connectedChannels = ocState.channels.size
        )

        // ── Hermes 统计 ──────────────────
        SectionHeader("Hermes 统计")
        HermesStatsCards(
            usageStats = hmState.usageStats,
            activeSessions = hmState.sessions.size
        )

        // ── 快捷操作 ─────────────────────
        SectionHeader("快捷操作")
        QuickActions(
            onStartAgent = {},
            onSwitchModel = {},
            onHealthCheck = {}
        )

        // ── 实时日志 ─────────────────────
        SectionHeader("实时日志")
        LogViewer(logs = ocState.logs)

        Spacer(Modifier.height(16.dp))
    }
}
