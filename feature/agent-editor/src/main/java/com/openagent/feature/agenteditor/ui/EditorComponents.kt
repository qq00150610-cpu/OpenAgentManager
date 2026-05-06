package com.openagent.feature.agenteditor.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openagent.core.model.*
import com.openagent.core.ui.components.AgentTypeChip

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
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: File name and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = scriptName,
                    onValueChange = onNameChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    label = { Text("文件名") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onNew) {
                    Icon(Icons.Default.Add, contentDescription = "新建")
                }
                IconButton(onClick = onSave) {
                    Icon(Icons.Default.Save, contentDescription = "保存")
                }
                IconButton(onClick = onTemplates) {
                    Icon(Icons.Default.LibraryBooks, contentDescription = "模板库")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Language, Target, Run
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language selector
                var langExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = langExpanded,
                    onExpandedChange = { langExpanded = !langExpanded }
                ) {
                    OutlinedTextField(
                        value = when (selectedLanguage) {
                            ScriptLanguage.PYTHON -> "Python"
                            ScriptLanguage.YAML -> "YAML"
                            ScriptLanguage.MARKDOWN -> "Markdown"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("语言") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(langExpanded) },
                        modifier = Modifier.menuAnchor().width(130.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(expanded = langExpanded, onDismissRequest = { langExpanded = false }) {
                        DropdownMenuItem(text = { Text("Python") }, onClick = { onLanguageChange(ScriptLanguage.PYTHON); langExpanded = false })
                        DropdownMenuItem(text = { Text("YAML") }, onClick = { onLanguageChange(ScriptLanguage.YAML); langExpanded = false })
                        DropdownMenuItem(text = { Text("Markdown") }, onClick = { onLanguageChange(ScriptLanguage.MARKDOWN); langExpanded = false })
                    }
                }

                // Target agent selector
                var targetExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = targetExpanded,
                    onExpandedChange = { targetExpanded = !targetExpanded }
                ) {
                    OutlinedTextField(
                        value = when (targetAgent) {
                            AgentType.OPENCLAW -> "OpenClaw"
                            AgentType.OPENHERMES -> "Hermes"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("目标") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(targetExpanded) },
                        modifier = Modifier.menuAnchor().width(130.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(expanded = targetExpanded, onDismissRequest = { targetExpanded = false }) {
                        DropdownMenuItem(text = { Text("OpenClaw") }, onClick = { onTargetChange(AgentType.OPENCLAW); targetExpanded = false })
                        DropdownMenuItem(text = { Text("Hermes") }, onClick = { onTargetChange(AgentType.OPENHERMES); targetExpanded = false })
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Run button
                Button(
                    onClick = onRun,
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("运行中...")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("运行")
                    }
                }
            }
        }
    }
}

@Composable
fun CodeEditor(
    content: String,
    onContentChange: (String) -> Unit,
    language: ScriptLanguage,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(content) { mutableStateOf(TextFieldValue(content)) }
    val lineCount = content.lines().size

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Line numbers
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                for (i in 1..lineCount.coerceAtLeast(1)) {
                    Text(
                        text = "$i",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            // Code input
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onContentChange(newValue.text)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun ExecutionConsole(
    isRunning: Boolean,
    logs: List<String>,
    result: ExecutionResult?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Terminal,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "运行控制台",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                if (result != null) {
                    val statusColor = if (result.exitCode == 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    Text(
                        text = if (result.exitCode == 0) "✓ 成功" else "✗ 失败 (${result.exitCode})",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${result.duration}ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Log output area
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 300.dp)
                    .background(
                        Color(0xFF1E1E1E),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                items(logs) { log ->
                    val color = when {
                        log.contains("[ERROR]") -> Color(0xFFEF5350)
                        log.contains("[WARN]") -> Color(0xFFFFA726)
                        log.contains("[INFO]") -> Color(0xFF66BB6A)
                        log.contains("[OUTPUT]") -> Color(0xFF42A5F5)
                        else -> Color(0xFFE0E0E0)
                    }
                    Text(
                        text = log,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        color = color,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }

                if (isRunning) {
                    item {
                        Text(
                            text = "█",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }
}

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
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "模板库",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "关闭")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TemplateCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategorySelect(category) },
                        label = {
                            Text(
                                text = when (category) {
                                    TemplateCategory.OPENCLAW_SKILL -> "OpenClaw"
                                    TemplateCategory.HERMES_AGENT -> "Hermes"
                                    TemplateCategory.PROMPT_TEMPLATE -> "提示词"
                                    TemplateCategory.GENERAL -> "通用"
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val filteredTemplates = templates.filter { it.category == selectedCategory }

            LazyColumn {
                items(filteredTemplates) { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onTemplateSelect(template) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = template.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = template.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = template.codeSnippet.take(100) + if (template.codeSnippet.length > 100) "..." else "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VariablePanel(
    variables: List<PromptVariable>,
    onAddVariable: (String, String, String) -> Unit,
    onRemoveVariable: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var varName by remember { mutableStateOf("") }
    var varPlaceholder by remember { mutableStateOf("") }
    var varDefault by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.width(250.dp).fillMaxHeight(),
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "变量面板",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "添加变量")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (variables.isEmpty()) {
                Text(
                    "暂无变量，点击 + 添加",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn {
                items(variables) { variable ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "{{${variable.name}}}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                if (variable.defaultValue.isNotEmpty()) {
                                    Text(
                                        text = "默认: ${variable.defaultValue}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { onRemoveVariable(variable.name) }) {
                                Icon(Icons.Default.Delete, contentDescription = "删除", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("添加变量") },
            text = {
                Column {
                    OutlinedTextField(
                        value = varName,
                        onValueChange = { varName = it },
                        label = { Text("变量名") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = varPlaceholder,
                        onValueChange = { varPlaceholder = it },
                        label = { Text("占位提示") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = varDefault,
                        onValueChange = { varDefault = it },
                        label = { Text("默认值") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (varName.isNotBlank()) {
                        onAddVariable(varName, varPlaceholder, varDefault)
                        varName = ""
                        varPlaceholder = ""
                        varDefault = ""
                        showAddDialog = false
                    }
                }) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
