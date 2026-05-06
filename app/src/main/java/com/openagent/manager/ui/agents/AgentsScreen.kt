package com.openagent.manager.ui.agents

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openagent.core.model.*
import com.openagent.core.ui.components.*
import com.openagent.core.ui.theme.*

/**
 * Agents 管理页面
 *
 * 两个子视图:
 * 1. 列表视图 - 搜索 + 类型筛选 + Agent 列表
 * 2. 详情视图 - 点击 Agent 进入详情配置
 */
@Composable
fun AgentsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf<AgentType?>(null) }
    var selectedAgent by remember { mutableStateOf<AgentInfo?>(null) }

    // 示例数据
    val agents = remember {
        listOf(
            AgentInfo("oc-1", "Main Agent", AgentType.OPENCLAW, AgentStatus.ONLINE, 3, listOf("web", "code", "file")),
            AgentInfo("oc-2", "Helper Bot", AgentType.OPENCLAW, AgentStatus.OFFLINE, 0, listOf("web")),
            AgentInfo("hm-1", "GPT-4 Agent", AgentType.OPENHERMES, AgentStatus.ONLINE, 5, listOf("chat", "analysis")),
            AgentInfo("hm-2", "Claude Agent", AgentType.OPENHERMES, AgentStatus.BUSY, 2, listOf("chat", "code")),
            AgentInfo("hm-3", "Qwen Agent", AgentType.OPENHERMES, AgentStatus.ERROR, 0, listOf("chat"))
        )
    }

    // 如有选中则显示详情页
    selectedAgent?.let { agent ->
        AgentDetailScreen(agent = agent, onBack = { selectedAgent = null })
        return
    }

    val filtered = agents.filter { a ->
        (filterType == null || a.type == filterType) &&
        (searchQuery.isEmpty() || a.name.contains(searchQuery, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 搜索栏
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("搜索 Agent...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty())
                    IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, "清除") }
            }
        )

        Spacer(Modifier.height(8.dp))

        // 类型筛选
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filterType == null, onClick = { filterType = null }, label = { Text("全部") })
            FilterChip(
                selected = filterType == AgentType.OPENCLAW,
                onClick = { filterType = if (filterType == AgentType.OPENCLAW) null else AgentType.OPENCLAW },
                label = { Text("OpenClaw") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OpenClawBlue.copy(alpha = 0.12f))
            )
            FilterChip(
                selected = filterType == AgentType.OPENHERMES,
                onClick = { filterType = if (filterType == AgentType.OPENHERMES) null else AgentType.OPENHERMES },
                label = { Text("Hermes") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = HermesPurple.copy(alpha = 0.12f))
            )
        }

        Spacer(Modifier.height(12.dp))

        // Agent 列表
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { agent ->
                AgentListItem(agent = agent, onClick = { selectedAgent = agent })
            }
        }
    }
}

// ── Agent 列表项 ─────────────────────────

@Composable
private fun AgentListItem(agent: AgentInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusIndicator(status = agent.status)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(agent.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AgentTypeChip(type = agent.type)
                    Spacer(Modifier.width(8.dp))
                    Text("${agent.activeSessions} 会话", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Agent 详情页 ─────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentDetailScreen(agent: AgentInfo, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(agent.name) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 基本信息
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("基本信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    DetailRow("ID", agent.id)
                    DetailRow("名称", agent.name)
                    DetailRow("类型", agent.type.name)
                    DetailRow("状态", agent.status.name)
                    DetailRow("活跃会话", "${agent.activeSessions}")
                }
            }

            // 能力列表
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("能力列表", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        agent.capabilities.forEach { cap ->
                            SuggestionChip(onClick = {}, label = { Text(cap) })
                        }
                    }
                }
            }

            // 配置区域（按类型区分）
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        if (agent.type == AgentType.OPENCLAW) "OpenClaw 配置" else "Hermes 配置",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    if (agent.type == AgentType.OPENCLAW) {
                        OutlinedTextField("", {}, label = { Text("模型选择") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField("", {}, label = { Text("心跳间隔 (秒)") }, modifier = Modifier.fillMaxWidth())
                    } else {
                        OutlinedTextField("", {}, label = { Text("模型供应商") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField("", {}, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField("", {}, label = { Text("系统提示词") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    }
                }
            }

            // 操作按钮
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)) {
                    Icon(Icons.Default.PlayArrow, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("启动")
                }
                OutlinedButton(onClick = {}, Modifier.weight(1f)) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("重启")
                }
                OutlinedButton(onClick = {}, Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed)) {
                    Icon(Icons.Default.Delete, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("删除")
                }
            }
        }
    }
}
