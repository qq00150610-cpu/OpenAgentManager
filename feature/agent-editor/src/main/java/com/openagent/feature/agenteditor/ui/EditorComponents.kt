package com.openagent.feature.agenteditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openagent.core.model.*
import com.openagent.core.ui.theme.*

// ═══════════════════════════════════════════
//  编辑器工具栏
// ═══════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    scriptName: String,
    onNameChange: (String) -> Unit,
    selectedLanguage: ScriptLanguage,
    onLanguageChange: (ScriptLanguage) -> Unit,
    targetAgent: AgentType,
    onTargetChange: (AgentType) -> Unit,
    isRunning: Boolean,
    onNew: () -> Unit,
    onSave: () -> Unit,
    onRun: () -> Unit,
    onTemplates: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth(), tonalElevation = 2.dp, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 第一行：文件名 + 操作按钮
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = scriptName, onValueChange = onNameChange,
                    modifier = Modifier.weight(1f), singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    label = { Text("文件名") }
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onNew) { Icon(Icons.Default.Add, "新建") }
                IconButton(onClick = onSave) { Icon(Icons.Default.Save, "保存") }
                IconButton(onClick = onTemplates) { Icon(Icons.AutoMirrored.Filled.LibraryBooks, "模板库") }
            }

            Spacer(Modifier.height(8.dp))

            // 第二行：语言选择 + 目标选择 + 运行按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 语言下拉
                var langExp by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = langExp, onExpandedChange = { langExp = !langExp }) {
                    OutlinedTextField(
                        value = selectedLanguage.displayName, onValueChange = {}, readOnly = true,
                        label = { Text("语言") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(langExp) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).width(130.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(expanded = langExp, onDismissRequest = { langExp = false }) {
                        ScriptLanguage.entries.forEach { lang ->
                            DropdownMenuItem(text = { Text(lang.displayName) },
                                onClick = { onLanguageChange(lang); langExp = false })
                        }
                    }
                }

                // 目标下拉
                var targetExp by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = targetExp, onExpandedChange = { targetExp = !targetExp }) {
                    OutlinedTextField(
                        value = if (targetAgent == AgentType.OPENCLAW) "OpenClaw" else "Hermes",
                        onValueChange = {}, readOnly = true,
                        label = { Text("目标") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(targetExp) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).width(130.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(expanded = targetExp, onDismissRequest = { targetExp = false }) {
                        DropdownMenuItem(text = { Text("OpenClaw") },
                            onClick = { onTargetChange(AgentType.OPENCLAW); targetExp = false })
                        DropdownMenuItem(text = { Text("Hermes") },
                            onClick = { onTargetChange(AgentType.OPENHERMES); targetExp = false })
                    }
                }

                Spacer(Modifier.weight(1f))

                // 运行按钮
                Button(
                    onClick = onRun, enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("运行中...")
                    } else {
                        Icon(Icons.Default.PlayArrow, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("运行 ▶")
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════
//  代码编辑器（带行号）
// ═══════════════════════════════════════════

@Composable
fun CodeEditor(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(content) { mutableStateOf(TextFieldValue(content)) }
    val lineCount = content.lines().size.coerceAtLeast(1)

    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), tonalElevation = 1.dp) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 行号列
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                for (i in 1..lineCount) {
                    Text(
                        "$i", style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace, fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            // 代码输入区
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { v -> textFieldValue = v; onContentChange(v.text) },
                modifier = Modifier.fillMaxSize().padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 20.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    }
}

// ═══════════════════════════════════════════
//  运行控制台
// ═══════════════════════════════════════════

@Composable
fun ExecutionConsole(
    isRunning: Boolean,
    logs: List<String>,
    result: ExecutionResult?,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), tonalElevation = 1.dp) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 标题栏
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Terminal, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("运行控制台", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                if (result != null) {
                    val color = if (result.exitCode == 0) StatusGreen else StatusRed
                    Text(
                        if (result.exitCode == 0) "✓ 成功" else "✗ 失败 (${result.exitCode})",
                        style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("${result.duration}ms", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))

            // 终端输出区
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 300.dp)
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                items(logs) { log ->
                    val color = when {
                        log.contains("[ERROR]") -> StatusRed
                        log.contains("[WARN]") -> StatusOrange
                        log.contains("[INFO]") -> StatusGreen
                        log.contains("[OUTPUT]") -> OpenClawBlueLight
                        else -> Color(0xFFE0E0E0)
                    }
                    Text(log, style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace, fontSize = 12.sp
                    ), color = color, modifier = Modifier.padding(vertical = 1.dp))
                }
                if (isRunning) {
                    item {
                        Text("█", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = StatusGreen)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════
//  模板库抽屉
// ═══════════════════════════════════════════

@Composable
fun TemplateDrawer(
    templates: List<AgentTemplate>,
    selectedCategory: TemplateCategory,
    onCategorySelect: (TemplateCategory) -> Unit,
    onTemplateSelect: (AgentTemplate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(300.dp).fillMaxHeight(),
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("模板库", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "关闭") }
            }

            Spacer(Modifier.height(12.dp))

            // 分类筛选
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TemplateCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategorySelect(cat) },
                        label = { Text(cat.displayName, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val filtered = templates.filter { it.category == selectedCategory }
            LazyColumn {
                items(filtered) { template ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .clickable { onTemplateSelect(template) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(template.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(template.description, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                template.codeSnippet.take(80) + if (template.codeSnippet.length > 80) "..." else "",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════
//  变量面板
// ═══════════════════════════════════════════

@Composable
fun VariablePanel(
    variables: List<PromptVariable>,
    onAdd: (String, String, String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var placeholder by remember { mutableStateOf("") }
    var default by remember { mutableStateOf("") }

    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("变量面板", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, "添加变量") }
            }

            if (variables.isEmpty()) {
                Text("暂无变量，点击 + 添加", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(variables) { v ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), shape = RoundedCornerShape(6.dp)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("{{${v.name}}}", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                if (v.defaultValue.isNotEmpty())
                                    Text("默认: ${v.defaultValue}", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { onRemove(v.name) }) {
                                Icon(Icons.Default.Delete, "删除", Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("添加变量") },
            text = {
                Column {
                    OutlinedTextField(name, { name = it }, label = { Text("变量名") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(placeholder, { placeholder = it }, label = { Text("占位提示") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(default, { default = it }, label = { Text("默认值") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) { onAdd(name, placeholder, default); name = ""; placeholder = ""; default = ""; showDialog = false }
                }) { Text("添加") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } }
        )
    }
}
