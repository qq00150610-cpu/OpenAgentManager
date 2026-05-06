package com.openagent.manager.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openagent.core.ui.components.SectionHeader
import com.openagent.feature.agenteditor.ui.*
import com.openagent.feature.agenteditor.viewmodel.AgentEditorViewModel

/**
 * Agent 编辑器页面
 *
 * 布局结构（从上到下）:
 * 1. 工具栏（文件名 + 语言 + 目标 + 运行按钮）
 * 2. 代码编辑器（带行号）
 * 3. 运行控制台（终端输出）
 * 4. 变量面板（可折叠）
 * 5. 模板库抽屉（右侧滑出，覆盖显示）
 */
@Composable
fun EditorScreen(
    viewModel: AgentEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── 工具栏 ─────────────────────
            EditorToolbar(
                scriptName = state.currentScript.name,
                onNameChange = viewModel::updateName,
                selectedLanguage = state.selectedLanguage,
                onLanguageChange = viewModel::selectLanguage,
                targetAgent = state.targetAgent,
                onTargetChange = viewModel::selectTarget,
                isRunning = state.isRunning,
                onNew = viewModel::newScript,
                onSave = viewModel::saveScript,
                onRun = viewModel::runScript,
                onTemplates = viewModel::toggleTemplates
            )

            // ── 代码编辑器 ─────────────────
            SectionHeader("代码编辑器")
            CodeEditor(
                content = state.currentScript.content,
                onContentChange = viewModel::updateContent,
                modifier = Modifier.height(350.dp)
            )

            // ── 运行控制台 ─────────────────
            SectionHeader("运行控制台")
            ExecutionConsole(
                isRunning = state.isRunning,
                logs = state.executionLogs,
                result = state.executionResult
            )

            // ── 变量面板（可折叠）────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = viewModel::toggleVariables) {
                    Icon(Icons.Default.DataObject, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (state.showVariablePanel) "隐藏变量" else "显示变量")
                }
            }
            if (state.showVariablePanel) {
                VariablePanel(
                    variables = state.promptVariables,
                    onAdd = viewModel::addVariable,
                    onRemove = viewModel::removeVariable
                )
            }

            Spacer(Modifier.height(80.dp))
        }

        // ── 模板库抽屉（右侧覆盖）──────────
        if (state.showTemplateDrawer) {
            Row(Modifier.fillMaxSize()) {
                Spacer(Modifier.weight(1f))
                TemplateDrawer(
                    templates = state.templates,
                    selectedCategory = state.selectedCategory,
                    onCategorySelect = viewModel::filterCategory,
                    onTemplateSelect = viewModel::selectTemplate,
                    onDismiss = viewModel::toggleTemplates
                )
            }
        }
    }
}
