package com.openagent.feature.openclaw.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
//  OpenClaw 连接卡片
// ═══════════════════════════════════════════

@Composable
fun OpenClawConnectionCard(
    connectionState: ConnectionStatus,
    gatewayUrl: String,
    onUrlChange: (String) -> Unit,
    token: String,
    onTokenChange: (String) -> Unit,
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
            // 头部：图标 + 标题 + 状态
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Hub,
                    contentDescription = null,
                    tint = OpenClawBlue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "OpenClaw Gateway",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                StatusIndicator(
                    status = when (connectionState) {
                        ConnectionStatus.CONNECTED -> AgentStatus.ONLINE
                        ConnectionStatus.CONNECTING -> AgentStatus.BUSY
                        ConnectionStatus.DISCONNECTED -> AgentStatus.OFFLINE
                        ConnectionStatus.ERROR -> AgentStatus.ERROR
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (connectionState) {
                        ConnectionStatus.CONNECTED -> "已连接"
                        ConnectionStatus.CONNECTING -> "连接中..."
                        ConnectionStatus.DISCONNECTED -> "未连接"
                        ConnectionStatus.ERROR -> "错误"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 输入字段
            OutlinedTextField(
                value = gatewayUrl,
                onValueChange = onUrlChange,
                label = { Text("Gateway URL") },
                placeholder = { Text("wss://your-gateway.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = connectionState != ConnectionStatus.CONNECTED
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = token,
                onValueChange = onTokenChange,
                label = { Text("认证 Token") },
                placeholder = { Text("输入认证令牌") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = connectionState != ConnectionStatus.CONNECTED
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 操作按钮
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (connectionState == ConnectionStatus.CONNECTED) {
                    Button(
                        onClick = onDisconnect,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                    ) {
                        Icon(Icons.Default.LinkOff, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("断开")
                    }
                } else {
                    Button(
                        onClick = onConnect,
                        enabled = connectionState != ConnectionStatus.CONNECTING
                    ) {
                        if (connectionState == ConnectionStatus.CONNECTING) {
                            CircularProgressIndicator(
                                Modifier.size(18.dp), strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Link, null, Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(4.dp))
                        Text("连接")
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════
//  OpenClaw 统计网格（2列）
// ═══════════════════════════════════════════

@Composable
fun OpenClawStatsGrid(
    activeSessions: Int,
    messageThroughput: Long,
    gatewayLatency: Long,
    connectedChannels: Int,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { StatCard("活跃会话", "$activeSessions", "当前连接", containerColor = Color(0xFFE3F2FD)) }
        item { StatCard("消息吞吐量", "$messageThroughput", "条/分钟", containerColor = Color(0xFFE8F5E9)) }
        item { StatCard("网关延迟", "${gatewayLatency}ms", "实时", containerColor = Color(0xFFFFF3E0)) }
        item { StatCard("已连接渠道", "$connectedChannels", "平台", containerColor = Color(0xFFF3E5F5)) }
    }
}

// ═══════════════════════════════════════════
//  实时日志查看器
// ═══════════════════════════════════════════

@Composable
fun LogViewer(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    var filterLevel by remember { mutableStateOf<LogLevel?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        // 筛选栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("搜索日志...") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            FilterChip(selected = filterLevel == null, onClick = { filterLevel = null }, label = { Text("全部") })
            FilterChip(
                selected = filterLevel == LogLevel.ERROR,
                onClick = { filterLevel = if (filterLevel == LogLevel.ERROR) null else LogLevel.ERROR },
                label = { Text("错误") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val filtered = logs.filter { log ->
            (filterLevel == null || log.level == filterLevel) &&
            (searchQuery.isEmpty() || log.message.contains(searchQuery, ignoreCase = true))
        }

        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
            items(filtered) { log ->
                LogItem(
                    level = log.level.name,
                    tag = log.tag,
                    message = log.message,
                    timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date(log.timestamp))
                )
            }
        }
    }
}

// ═══════════════════════════════════════════
//  快捷操作栏
// ═══════════════════════════════════════════

@Composable
fun QuickActions(
    onStartAgent: () -> Unit,
    onSwitchModel: () -> Unit,
    onHealthCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(onClick = onStartAgent, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.PlayArrow, null, Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("启动 Agent")
        }
        OutlinedButton(onClick = onSwitchModel, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.SwapHoriz, null, Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("切换模型")
        }
        OutlinedButton(onClick = onHealthCheck, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.HealthAndSafety, null, Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("诊断")
        }
    }
}
