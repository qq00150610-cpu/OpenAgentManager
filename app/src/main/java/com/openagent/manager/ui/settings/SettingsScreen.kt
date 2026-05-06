package com.openagent.manager.ui.settings

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.openagent.core.ui.components.DetailRow

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
fun SettingsScreen() {
    var gatewayUrl by remember { mutableStateOf("") }
    var gatewayToken by remember { mutableStateOf("") }
    var hermesUrl by remember { mutableStateOf("") }
    var hermesApiKey by remember { mutableStateOf("") }
    var isDarkMode by remember { mutableStateOf(false) }
    var isBiometric by remember { mutableStateOf(false) }
    var isEncryption by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("中文") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
