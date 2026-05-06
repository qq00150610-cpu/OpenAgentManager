package com.openagent.feature.openhermes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openagent.core.model.*
import com.openagent.core.ui.components.*
import com.openagent.core.ui.theme.*

// ═══════════════════════════════════════════
//  Hermes 连接卡片
// ═══════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HermesConnectionCard(
    isConnected: Boolean,
    isConnecting: Boolean,
    serverUrl: String,
    onUrlChange: (String) -> Unit,
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    apiMode: ApiMode,
    onApiModeChange: (ApiMode) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 头部
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.SmartToy, null, tint = HermesPurple, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Text("Hermes Agent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                StatusIndicator(
                    status = if (isConnected) AgentStatus.ONLINE
                    else if (isConnecting) AgentStatus.BUSY
                    else AgentStatus.OFFLINE
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "已连接" else if (isConnecting) "连接中..." else "未连接",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = serverUrl, onValueChange = onUrlChange,
                label = { Text("Hermes 后端地址") },
                placeholder = { Text("http://localhost:8080") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !isConnected
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = apiKey, onValueChange = onApiKeyChange,
                label = { Text("API Key") },
                placeholder = { Text("输入 API Key") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !isConnected
            )
            Spacer(Modifier.height(8.dp))

            // API 模式选择
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = when (apiMode) {
                        ApiMode.CHAT_COMPLETIONS -> "Chat Completions"
                        ApiMode.CODEX_RESPONSES -> "Codex Responses"
                        ApiMode.ANTHROPIC_MESSAGES -> "Anthropic Messages"
                    },
                    onValueChange = {}, readOnly = true,
                    label = { Text("API 模式") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isConnected
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Chat Completions") },
                        onClick = { onApiModeChange(ApiMode.CHAT_COMPLETIONS); expanded = false })
                    DropdownMenuItem(text = { Text("Codex Responses") },
                        onClick = { onApiModeChange(ApiMode.CODEX_RESPONSES); expanded = false })
                    DropdownMenuItem(text = { Text("Anthropic Messages") },
                        onClick = { onApiModeChange(ApiMode.ANTHROPIC_MESSAGES); expanded = false })
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (isConnected) {
                    Button(onClick = onDisconnect, colors = ButtonDefaults.buttonColors(containerColor = StatusRed)) {
                        Icon(Icons.Default.LinkOff, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("断开")
                    }
                } else {
                    Button(onClick = onConnect, enabled = !isConnecting) {
                        if (isConnecting) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("连接")
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════
//  模型列表
// ═══════════════════════════════════════════

@Composable
fun ModelList(
    models: List<ModelInfo>,
    selectedModel: String,
    onModelSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("可用模型", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            if (models.isEmpty()) {
                Text("暂无可用模型", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(models) { model ->
                        ListItem(
                            headlineContent = { Text(model.name) },
                            supportingContent = { Text(model.provider) },
                            leadingContent = {
                                RadioButton(selected = model.id == selectedModel, onClick = { onModelSelect(model.id) })
                            },
                            modifier = Modifier.clickable { onModelSelect(model.id) }
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════
//  Hermes 统计卡片（水平排列）
// ═══════════════════════════════════════════

@Composable
fun HermesStatsCards(
    usageStats: UsageStats,
    activeSessions: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(
            title = "Token 消耗",
            value = formatTokenCount(usageStats.totalTokens),
            modifier = Modifier.weight(1f),
            containerColor = Color(0xFFF3E5F5)
        )
        StatCard(
            title = "活跃会话",
            value = "$activeSessions",
            modifier = Modifier.weight(1f),
            containerColor = Color(0xFFE8F5E9)
        )
        StatCard(
            title = "成本预估",
            value = "$${String.format("%.2f", usageStats.totalCost)}",
            modifier = Modifier.weight(1f),
            containerColor = Color(0xFFFFF3E0)
        )
    }
}

private fun formatTokenCount(count: Long): String = when {
    count >= 1_000_000 -> "${String.format("%.1f", count / 1_000_000.0)}M"
    count >= 1_000 -> "${String.format("%.1f", count / 1_000.0)}K"
    else -> "$count"
}
