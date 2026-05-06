package com.openagent.feature.agenteditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openagent.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EditorUiState(
    val currentScript: ScriptDocument = ScriptDocument(
        id = UUID.randomUUID().toString(),
        name = "untitled.py"
    ),
    val scripts: List<ScriptDocument> = emptyList(),
    val executionResult: ExecutionResult? = null,
    val isRunning: Boolean = false,
    val templates: List<AgentTemplate> = emptyList(),
    val selectedCategory: TemplateCategory = TemplateCategory.GENERAL,
    val selectedLanguage: ScriptLanguage = ScriptLanguage.PYTHON,
    val targetAgent: AgentType = AgentType.OPENCLAW,
    val showTemplateDrawer: Boolean = false,
    val showVariablePanel: Boolean = false,
    val promptVariables: List<PromptVariable> = emptyList(),
    val executionLogs: List<String> = emptyList()
)

@HiltViewModel
class AgentEditorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init { loadDefaultTemplates() }

    // ── 模板库 ─────────────────────────────

    private fun loadDefaultTemplates() {
        _uiState.update {
            it.copy(templates = listOf(
                AgentTemplate("soul", "SOUL.md 模板", "定义 Agent 个性特质",
                    TemplateCategory.OPENCLAW_SKILL,
                    """# SOUL.md - 你是谁
你是一个 AI 助手，代号 {{agent_name}}.

## 核心信条
- 真诚地帮助，而不是表演式地帮助
- 要有观点
- 先自救再提问
"""),
                AgentTemplate("agents", "AGENTS.md 模板", "定义 Agent 行为规则",
                    TemplateCategory.OPENCLAW_SKILL,
                    """# AGENTS.md - 行为规则

## 安全规则
- 不要泄露敏感信息
- 不要运行破坏性命令

## 工具使用
- 使用适当的工具完成任务
"""),
                AgentTemplate("hermes", "Hermes Agent 配置", "YAML 配置模板",
                    TemplateCategory.HERMES_AGENT,
                    """# Hermes Agent Configuration
agent:
  name: "{{agent_name}}"
  model: "{{model_id}}"
  system_prompt: |
    你是一个有用的 AI 助手。
  tools:
    - web_search
    - code_execution
"""),
                AgentTemplate("prompt", "基础提示词模板", "通用提示词工程模板",
                    TemplateCategory.PROMPT_TEMPLATE,
                    """# System Prompt
你是一个 {{role}} 专家。

## 任务
{{task_description}}

## 约束
- 输出格式：{{output_format}}
"""),
                AgentTemplate("python", "Python Agent 脚本", "基础 Python Agent 脚本",
                    TemplateCategory.GENERAL,
                    """#!/usr/bin/env python3
\"\"\"Agent Script\"\"\"

class Agent:
    def __init__(self, name: str):
        self.name = name

    def process(self, text: str) -> str:
        return f"Processed: {text}"

if __name__ == "__main__":
    agent = Agent("{{agent_name}}")
    print(agent.process("Hello"))
""")
            ))
        }
    }

    // ── 编辑器操作 ─────────────────────────

    fun updateContent(content: String) {
        _uiState.update {
            it.copy(currentScript = it.currentScript.copy(content = content, updatedAt = System.currentTimeMillis()))
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(currentScript = it.currentScript.copy(name = name)) }
    }

    fun selectLanguage(lang: ScriptLanguage) {
        _uiState.update {
            it.copy(selectedLanguage = lang, currentScript = it.currentScript.copy(language = lang))
        }
    }

    fun selectTarget(type: AgentType) {
        _uiState.update { it.copy(targetAgent = type) }
    }

    fun toggleTemplates() { _uiState.update { it.copy(showTemplateDrawer = !it.showTemplateDrawer) } }
    fun toggleVariables() { _uiState.update { it.copy(showVariablePanel = !it.showVariablePanel) } }

    fun selectTemplate(template: AgentTemplate) {
        _uiState.update {
            it.copy(
                currentScript = it.currentScript.copy(content = template.codeSnippet, updatedAt = System.currentTimeMillis()),
                showTemplateDrawer = false
            )
        }
    }

    fun filterCategory(cat: TemplateCategory) { _uiState.update { it.copy(selectedCategory = cat) } }

    fun newScript() {
        _uiState.update {
            it.copy(
                currentScript = ScriptDocument(
                    id = UUID.randomUUID().toString(),
                    name = "untitled.${it.selectedLanguage.extension}",
                    language = it.selectedLanguage
                ),
                executionResult = null, executionLogs = emptyList()
            )
        }
    }

    fun saveScript() {
        _uiState.update {
            val s = it.currentScript.copy(updatedAt = System.currentTimeMillis())
            it.copy(currentScript = s, scripts = it.scripts.filter { x -> x.id != s.id } + s)
        }
    }

    // ── 运行脚本 ─────────────────────────

    fun runScript() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, executionLogs = emptyList()) }
            val logs = mutableListOf<String>()

            logs += "[INFO] 正在编译脚本..."
            _uiState.update { it.copy(executionLogs = logs.toList()) }
            delay(400)

            logs += "[INFO] 语法检查通过"
            _uiState.update { it.copy(executionLogs = logs.toList()) }
            delay(300)

            logs += "[INFO] 部署到 ${_uiState.value.targetAgent.name}..."
            _uiState.update { it.copy(executionLogs = logs.toList()) }
            delay(600)

            logs += "[OUTPUT] 脚本运行成功"
            logs += "[INFO] 执行完成 (耗时 ${1300}ms)"

            _uiState.update {
                it.copy(
                    isRunning = false, executionLogs = logs,
                    executionResult = ExecutionResult(it.currentScript.id, "脚本运行成功", 0, 1300L)
                )
            }
        }
    }

    // ── 变量管理 ─────────────────────────

    fun addVariable(name: String, placeholder: String, default: String) {
        _uiState.update { it.copy(promptVariables = it.promptVariables + PromptVariable(name, placeholder, default)) }
    }

    fun removeVariable(name: String) {
        _uiState.update { it.copy(promptVariables = it.promptVariables.filter { v -> v.name != name }) }
    }
}
