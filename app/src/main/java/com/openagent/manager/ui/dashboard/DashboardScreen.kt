package com.openagent.manager.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
 * 1. 免费 API 快速配置（顶部醒目位置）
 * 2. OpenClaw 连接卡片
 * 3. Hermes 连接卡片
 * 4. OpenClaw 统计网格 (2x2)
 * 5. Hermes 统计卡片 (1x3)
 * 6. 快捷操作栏
 * 7. 实时日志查看器
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
    
    // 免费 API 提供商相关状态
    var showFreeApiSelector by remember { mutableStateOf(false) }
    var selectedProviderId by remember { mutableStateOf("") }
    var freeApiKeyInput by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var configSuccessMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ═══════════════════════════════════════════
        //  免费 AI API 快速配置（仪表盘顶部醒目标识）
        // ═══════════════════════════════════════════
        val selectedProvider = FREE_API_PROVIDERS.find { it.id == selectedProviderId }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RocketLaunch, null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("🚀 免费 AI API 快速配置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                
                if (selectedProvider != null) {
                    // 已选择提供商
                    Text("当前: ${selectedProvider.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text(selectedProvider.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = freeApiKeyInput,
                        onValueChange = { freeApiKeyInput = it },
                        label = { Text("API Key") },
                        placeholder = { Text(selectedProvider.apiKeyHint) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                hermesVm.configure(selectedProvider.baseUrl, freeApiKeyInput)
                                hermesVm.testConnection()
                                configSuccessMsg = "已应用 ${selectedProvider.name}，正在测试..."
                            },
                            modifier = Modifier.weight(1f),
                            enabled = freeApiKeyInput.isNotBlank()
                        ) {
                            Icon(Icons.Default.PlayArrow, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("应用并测试")
                        }
                        OutlinedButton(
                            onClick = { showFreeApiSelector = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SwapHoriz, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("切换")
                        }
                    }
                } else {
                    // 未选择提供商
                    Text("一键接入免费 AI 模型，无需自建服务器",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { showFreeApiSelector = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.List, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("选择免费 API 提供商")
                    }
                }
                
                configSuccessMsg?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary)
                }
                
                if (hmState.isConnected) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("已连接", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        
        // 免费 API 提供商选择弹窗
        if (showFreeApiSelector) {
            DashboardFreeApiDialog(
                onDismiss = { showFreeApiSelector = false },
                onSelect = { provider ->
                    selectedProviderId = provider.id
                    freeApiKeyInput = ""
                    selectedModel = provider.models.firstOrNull() ?: ""
                    showFreeApiSelector = false
                    configSuccessMsg = null
                }
            )
        }

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

// ═══════════════════════════════════════════
//  仪表盘用免费 API 提供商选择弹窗
// ═══════════════════════════════════════════

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DashboardFreeApiDialog(
    onDismiss: () -> Unit,
    onSelect: (FreeApiProvider) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RocketLaunch, null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("选择免费 API 提供商")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("精选 10 个免费 AI API 提供商（支持 OpenAI 兼容格式）：",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                
                FREE_API_PROVIDERS.forEach { provider ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(provider) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(provider.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f))
                                AssistChip(
                                    onClick = {},
                                    label = { Text("免费") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(provider.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
