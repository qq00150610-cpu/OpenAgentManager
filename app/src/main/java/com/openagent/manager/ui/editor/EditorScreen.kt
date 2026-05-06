package com.openagent.manager.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openagent.core.model.*
import com.openagent.core.ui.components.SectionHeader
import com.openagent.feature.agenteditor.ui.*
import com.openagent.feature.agenteditor.viewmodel.AgentEditorViewModel

@Composable
fun EditorScreen(
    viewModel: AgentEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Toolbar
            EditorToolbar(
                scriptName = uiState.currentScript.name,
                onNameChange = viewModel::updateScriptName,
                selectedLanguage = uiState.selectedLanguage,
                onLanguageChange = viewModel::selectLanguage,
                targetAgent = uiState.targetAgent,
                onTargetChange = viewModel::selectTargetAgent,
                isRunning = uiState.isRunning,
                onNew = viewModel::newScript,
                onSave = viewModel::saveScript,
                onRun = viewModel::runScript,
                onTemplates = viewModel::toggleTemplateDrawer
            )

            // Code Editor
            SectionHeader("代码编辑器")
            CodeEditor(
                content = uiState.currentScript.content,
                onContentChange = viewModel::updateScriptContent,
                language = uiState.selectedLanguage,
                modifier = Modifier.height(350.dp)
            )

            // Execution Console
            SectionHeader("运行控制台")
            ExecutionConsole(
                isRunning = uiState.isRunning,
                logs = uiState.executionLogs,
                result = uiState.executionResult,
                modifier = Modifier.fillMaxWidth()
            )

            // Variable Panel Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = viewModel::toggleVariablePanel) {
                    Icon(Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (uiState.showVariablePanel) "隐藏变量" else "显示变量")
                }
            }

            if (uiState.showVariablePanel) {
                VariablePanel(
                    variables = uiState.promptVariables,
                    onAddVariable = viewModel::addVariable,
                    onRemoveVariable = viewModel::removeVariable,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Template Drawer Overlay
        if (uiState.showTemplateDrawer) {
            Row(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                TemplateDrawer(
                    templates = uiState.templates,
                    selectedCategory = uiState.selectedTemplateCategory,
                    onCategorySelect = viewModel::filterTemplates,
                    onTemplateSelect = viewModel::selectTemplate,
                    onDismiss = viewModel::toggleTemplateDrawer
                )
            }
        }
    }
}
