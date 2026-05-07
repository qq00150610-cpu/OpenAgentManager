package com.openagent.manager.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInBrowser
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import com.openagent.core.model.*
import com.openagent.core.ui.components.DetailRow
import com.openagent.feature.openhermes.viewmodel.HermesViewModel

/**
 * 设置页面
 *
 * 布局结构（从上到下）:
 * 1. API 连接配置（OpenClaw + Hermes）
 * 2. 自动发现（QR 码 + mDNS）
 * 3. 主题与显示（深色模式、语言）
 * 4. 安全设置（生物识别、加密）
 * 5. 本地存储管理
 * 6. 关于
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreen(
    hermesVm: HermesViewModel = hiltViewModel()
) {
    var gatewayUrl by remember { mutableStateOf("") }
    var gatewayToken by remember { mutableStateOf("") }
    var hermesUrl by remember { mutableStateOf("") }
    var hermesApiKey by remember { mutableStateOf("") }
    var isDarkMode by remember { mutableStateOf(false) }
    var isBiometric by remember { mutableStateOf(false) }
    var isEncryption by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("中文") }
    
    // 免费 API 提供商相关状态
    var showFreeApiSelector by remember { mutableStateOf(false) }
    var selectedProviderId by remember { mutableStateOf("") }
    var freeApiKeyInput by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var showModelSelector by remember { mutableStateOf(false) }
    var configSuccess by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ═══════════════════════════════════════════
        //  免费 API 提供商配置（新增 - 最上面，最醒目）
        // ═══════════════════════════════════════════
        SettingsSection("🎯 免费 AI API 快速配置", Icons.Default.RocketLaunch) {
            Text(
                "一键配置免费 AI 模型 API，无需自建服务器",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            // 已选择的提供商
            val selectedProvider = FREE_API_PROVIDERS.find { it.id == selectedProviderId }
            
            if (selectedProvider != null) {
                // 已选择提供商时的状态展示
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(selectedProvider.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(selectedProvider.description,
                            style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Text("API 地址: ${selectedProvider.baseUrl}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // 选择提供商按钮
            OutlinedButton(
                onClick = { showFreeApiSelector = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.List, null, Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (selectedProvider == null) "选择免费 API 提供商" else "切换提供商")
            }

            if (selectedProvider != null) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = freeApiKeyInput,
                    onValueChange = { freeApiKeyInput = it },
                    label = { Text("${selectedProvider.name} API Key") },
                    placeholder = { Text(selectedProvider.apiKeyHint) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                // 模型选择
                OutlinedButton(
                    onClick = { showModelSelector = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.SmartToy, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (selectedModel.isEmpty()) "选择模型" else "模型: $selectedModel")
                }

                Spacer(Modifier.height(12.dp))

                // 应用配置按钮
                Button(
                    onClick = {
                        hermesVm.configure(selectedProvider.baseUrl, freeApiKeyInput)
                        hermesVm.testConnection()
                        configSuccess = "已应用 ${selectedProvider.name} 配置，开始测试连接..."
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = freeApiKeyInput.isNotBlank()
                ) {
                    Icon(Icons.Default.PlayArrow, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("应用并测试连接")
                }

                // 打开官网
                if (selectedProvider.officialSite.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.AutoMirrored.Filled.OpenInBrowser, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("前往 ${selectedProvider.name} 官网获取 API Key", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 配置成功提示
            configSuccess?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(msg, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary)
            }
        }

        // ── 免费 API 提供商选择弹窗 ──────
        if (showFreeApiSelector) {
            FreeApiProviderDialog(
                onDismiss = { showFreeApiSelector = false },
                onSelect = { provider ->
                    selectedProviderId = provider.id
                    freeApiKeyInput = ""
                    selectedModel = provider.models.firstOrNull() ?: ""
                    showFreeApiSelector = false
                    configSuccess = null
                }
            )
        }

        // ── 模型选择弹窗 ──────
        if (showModelSelector) {
            val provider = FREE_API_PROVIDERS.find { it.id == selectedProviderId }
            if (provider != null && provider.models.isNotEmpty()) {
                ModelSelectionDialog(
                    models = provider.models,
                    currentModel = selectedModel,
                    onDismiss = { showModelSelector = false },
                    onSelect = { model ->
                        selectedModel = model
                        showModelSelector = false
                    }
                )
            }
        }

        // ── API 连接配置 ─────────────────
        SettingsSection("API 连接配置", Icons.Default.Cloud) {
            OutlinedTextField(gatewayUrl, { gatewayUrl = it },
                label = { Text("OpenClaw Gateway URL") }, placeholder = { Text("wss://your-gateway.com") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(gatewayToken, { gatewayToken = it },
                label = { Text("Gateway 认证 Token") }, placeholder = { Text("输入认证令牌") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(hermesUrl, { hermesUrl = it },
                label = { Text("Hermes Agent 后端地址") }, placeholder = { Text("http://localhost:8080") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(hermesApiKey, { hermesApiKey = it },
                label = { Text("Hermes API Key") }, placeholder = { Text("输入 API Key") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}, Modifier.weight(1f)) {
                    Icon(Icons.Default.WifiFind, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("测试 OpenClaw")
                }
                OutlinedButton(onClick = {}, Modifier.weight(1f)) {
                    Icon(Icons.Default.WifiFind, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("测试 Hermes")
                }
            }
        }

        // ── 自动发现 ─────────────────────
        SettingsSection("自动发现", Icons.Default.QrCodeScanner) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}, Modifier.weight(1f)) {
                    Icon(Icons.Default.QrCode, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("扫描 QR 码")
                }
                OutlinedButton(onClick = {}, Modifier.weight(1f)) {
                    Icon(Icons.Default.Dns, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("mDNS 发现")
                }
            }
        }

        // ── 主题与显示 ───────────────────
        SettingsSection("主题与显示", Icons.Default.Palette) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("深色模式", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
            }
            var langExp by remember { mutableStateOf(false) }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("语言", style = MaterialTheme.typography.bodyLarge)
                ExposedDropdownMenuBox(expanded = langExp, onExpandedChange = { langExp = !langExp }) {
                    OutlinedTextField(language, {}, readOnly = true, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).width(120.dp),
                        textStyle = MaterialTheme.typography.bodyMedium)
                    ExposedDropdownMenu(expanded = langExp, onDismissRequest = { langExp = false }) {
                        DropdownMenuItem(text = { Text("中文") }, onClick = { language = "中文"; langExp = false })
                        DropdownMenuItem(text = { Text("English") }, onClick = { language = "English"; langExp = false })
                    }
                }
            }
        }

        // ── 安全设置 ─────────────────────
        SettingsSection("安全设置", Icons.Default.Security) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("生物识别锁", style = MaterialTheme.typography.bodyLarge)
                    Text("指纹/面部解锁", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = isBiometric, onCheckedChange = { isBiometric = it })
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("数据加密", style = MaterialTheme.typography.bodyLarge)
                    Text("加密本地存储数据", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = isEncryption, onCheckedChange = { isEncryption = it })
            }
        }

        // ── 本地存储 ─────────────────────
        SettingsSection("本地存储", Icons.Default.Storage) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}, Modifier.weight(1f)) {
                    Icon(Icons.Default.CleaningServices, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("清除缓存")
                }
                OutlinedButton(onClick = {}, Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.DeleteForever, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("清除所有数据")
                }
            }
        }

        // ── 关于 ─────────────────────────
        SettingsSection("关于", Icons.Default.Info) {
            DetailRow("版本", "1.0.0")
            DetailRow("构建号", "1")
            DetailRow("开源许可", "MIT License")
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {}, Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Description, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("依赖库列表")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ── 设置区块卡片 ─────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// ═══════════════════════════════════════════
//  免费 API 提供商选择弹窗
// ═══════════════════════════════════════════

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FreeApiProviderDialog(
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
                Text("以下是精选的 10 个免费 AI API 提供商，支持 OpenAI 兼容格式：",
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
                                    label = { Text("免费", style = MaterialTheme.typography.labelSmall) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(provider.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("地址: ${provider.baseUrl}",
                                style = MaterialTheme.typography.labelSmall,
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

// ═══════════════════════════════════════════
//  模型选择弹窗
// ═══════════════════════════════════════════

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ModelSelectionDialog(
    models: List<String>,
    currentModel: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SmartToy, null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("选择模型")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                models.forEach { model ->
                    val isSelected = model == currentModel
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(model) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(model, style = MaterialTheme.typography.bodyMedium)
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
