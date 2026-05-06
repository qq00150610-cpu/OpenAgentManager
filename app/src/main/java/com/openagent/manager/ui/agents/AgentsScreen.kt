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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openagent.core.model.*
import com.openagent.core.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf<AgentType?>(null) }
    var selectedAgent by remember { mutableStateOf<AgentInfo?>(null) }

    // Sample data
    val agents = remember {
        listOf(
            AgentInfo("oc-1", "Main Agent", AgentType.OPENCLAW, AgentStatus.ONLINE, 3, listOf("web", "code", "file")),
            AgentInfo("oc-2", "Helper Bot", AgentType.OPENCLAW, AgentStatus.OFFLINE, 0, listOf("web")),
            AgentInfo("hm-1", "GPT-4 Agent", AgentType.OPENHERMES, AgentStatus.ONLINE, 5, listOf("chat", "analysis")),
            AgentInfo("hm-2", "Claude Agent", AgentType.OPENHERMES, AgentStatus.BUSY, 2, listOf("chat", "code")),
            AgentInfo("hm-3", "Qwen Agent", AgentType.OPENHERMES, AgentStatus.ERROR, 0, listOf("chat"))
        )
    }

    val filteredAgents = agents.filter { agent ->
        (filterType == null || agent.type == filterType) &&
        (searchQuery.isEmpty() || agent.name.contains(searchQuery, ignoreCase = true))
    }

    if (selectedAgent != null) {
        AgentDetailScreen(
            agent = selectedAgent!!,
            onBack = { selectedAgent = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search and filter
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("搜索 Agent...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "清除")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterType == null,
                onClick = { filterType = null },
                label = { Text("全部") }
            )
            FilterChip(
                selected = filterType == AgentType.OPENCLAW,
                onClick = { filterType = if (filterType == AgentType.OPENCLAW) null else AgentType.OPENCLAW },
                label = { Text("OpenClaw") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1976D2).copy(alpha = 0.12f)
                )
            )
            FilterChip(
                selected = filterType == AgentType.OPENHERMES,
                onClick = { filterType = if (filterType == AgentType.OPENHERMES) null else AgentType.OPENHERMES },
                label = { Text("Hermes") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF7B1FA2).copy(alpha = 0.12f)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredAgents) { agent ->
                AgentListItem(
                    agent = agent,
                    onClick = { selectedAgent = agent }
                )
            }
        }
    }
}

@Composable
fun AgentListItem(
    agent: AgentInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusIndicator(status = agent.status)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = agent.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AgentTypeChip(type = agent.type)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${agent.activeSessions} 会话",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDetailScreen(
    agent: AgentInfo,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(agent.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("基本信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("ID", agent.id)
                    DetailRow("名称", agent.name)
                    DetailRow("类型", agent.type.name)
                    DetailRow("状态", agent.status.name)
                    DetailRow("活跃会话", "${agent.activeSessions}")
                }
            }

            // Capabilities
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("能力列表", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        agent.capabilities.forEach { cap ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(cap) }
                            )
                        }
                    }
                }
            }

            // Config based on type
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (agent.type == AgentType.OPENCLAW) "OpenClaw 配置" else "Hermes 配置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (agent.type == AgentType.OPENCLAW) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("模型选择") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("心跳间隔 (秒)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("模型供应商") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Base URL") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("系统提示词") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("启动")
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("重启")
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("删除")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
