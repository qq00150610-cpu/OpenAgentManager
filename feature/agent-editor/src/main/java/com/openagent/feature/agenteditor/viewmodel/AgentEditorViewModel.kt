package com.openagent.feature.agenteditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openagent.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EditorUiState(
    val currentScript: ScriptDocument = ScriptDocument(
        id = UUID.randomUUID().toString(),
        name = "untitled.py",
        content = ""
    ),
    val scripts: List<ScriptDocument> = emptyList(),
    val executionResult: ExecutionResult? = null,
    val isRunning: Boolean = false,
    val templates: List<AgentTemplate> = emptyList(),
    val selectedTemplateCategory: TemplateCategory = TemplateCategory.GENERAL,
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

    init {
        loadDefaultTemplates()
    }

    private fun loadDefaultTemplates() {
        val templates = listOf(
            AgentTemplate(
                id = "soul_template",
                name = "SOUL.md 模板",
                description = "定义 Agent 个性特质",
                category = TemplateCategory.OPENCLAW_SKILL,
                codeSnippet = """# SOUL.md - 你是谁

你是一个 AI 助手，代号 {{agent_name}}.

## 核心信条
- 真诚地帮助，而不是表演式地帮助
- 要有观点
- 先自救再提问
"""
            ),
            AgentTemplate(
                id = "agents_template",
                name = "AGENTS.md 模板",
                description = "定义 Agent 行为规则",
                category = TemplateCategory.OPENCLAW_SKILL,
                codeSnippet = """# AGENTS.md - 行为规则

## 安全规则
- 不要泄露敏感信息
- 不要运行破坏性命令

## 工具使用
- 使用适当的工具完成任务
- 记录重要操作
"""
            ),
            AgentTemplate(
                id = "hermes_config",
                name = "Hermes Agent 配置",
                description = "Hermes Agent YAML 配置模板",
                category = TemplateCategory.HERMES_AGENT,
                codeSnippet = """# Hermes Agent Configuration
agent:
  name: "{{agent_name}}"
  model: "{{model_id}}"
  system_prompt: |
    你是一个有用的 AI 助手。
  tools:
    - web_search
    - code_execution
  learning:
    enabled: true
    strategy: "closed_loop"
"""
            ),
            AgentTemplate(
                id = "prompt_basic",
                name = "基础提示词模板",
                description = "通用提示词工程模板",
                category = TemplateCategory.PROMPT_TEMPLATE,
                codeSnippet = """# System Prompt

你是一个 {{role}} 专家。

## 任务
{{task_description}}

## 约束
- 输出格式：{{output_format}}
- 语言：{{language}}
"""
            ),
            AgentTemplate(
                id = "python_agent",
                name = "Python Agent 脚本",
                description = "基础 Python Agent 脚本",
                category = TemplateCategory.GENERAL,
                codeSnippet = """#!/usr/bin/env python3
"""Agent Script for OpenClaw/Hermes"""

import asyncio
from typing import Optional

class Agent:
    def __init__(self, name: str):
        self.name = name
    
    async def process(self, input_text: str) -> str:
        """处理输入并返回响应"""
        # TODO: 实现处理逻辑
        return f"Processed: {input_text}"
    
    async def run(self):
        """主运行循环"""
        print(f"Agent {self.name} started")
        while True:
            try:
                user_input = input("> ")
                if user_input.lower() in ("quit", "exit"):
                    break
                result = await self.process(user_input)
                print(result)
            except EOFError:
                break
        print(f"Agent {self.name} stopped")

if __name__ == "__main__":
    agent = Agent("{{agent_name}}")
    asyncio.run(agent.run())
"""
            )
        )
        _uiState.update { it.copy(templates = templates) }
    }

    fun updateScriptContent(content: String) {
        _uiState.update {
            it.copy(currentScript = it.currentScript.copy(
                content = content,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    fun updateScriptName(name: String) {
        _uiState.update {
            it.copy(currentScript = it.currentScript.copy(name = name))
        }
    }

    fun selectLanguage(language: ScriptLanguage) {
        _uiState.update {
            it.copy(
                selectedLanguage = language,
                currentScript = it.currentScript.copy(language = language)
            )
        }
    }

    fun selectTargetAgent(type: AgentType) {
        _uiState.update { it.copy(targetAgent = type) }
    }

    fun toggleTemplateDrawer() {
        _uiState.update { it.copy(showTemplateDrawer = !it.showTemplateDrawer) }
    }

    fun toggleVariablePanel() {
        _uiState.update { it.copy(showVariablePanel = !it.showVariablePanel) }
    }

    fun selectTemplate(template: AgentTemplate) {
        _uiState.update {
            it.copy(
                currentScript = it.currentScript.copy(
                    content = template.codeSnippet,
                    updatedAt = System.currentTimeMillis()
                ),
                showTemplateDrawer = false
            )
        }
    }

    fun filterTemplates(category: TemplateCategory) {
        _uiState.update { it.copy(selectedTemplateCategory = category) }
    }

    fun runScript() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, executionLogs = emptyList()) }
            
            // Simulate execution
            val logs = mutableListOf<String>()
            logs.add("[INFO] 正在编译脚本...")
            _uiState.update { it.copy(executionLogs = logs.toList()) }
            
            kotlinx.coroutines.delay(500)
            logs.add("[INFO] 语法检查通过")
            _uiState.update { it.copy(executionLogs = logs.toList()) }
            
            kotlinx.coroutines.delay(300)
            logs.add("[INFO] 正在部署到 ${_uiState.value.targetAgent.name}...")
            _uiState.update { it.copy(executionLogs = logs.toList()) }
            
            kotlinx.coroutines.delay(800)
            logs.add("[INFO] 执行完成")
            logs.add("[OUTPUT] 脚本运行成功")
            
            _uiState.update {
                it.copy(
                    isRunning = false,
                    executionLogs = logs,
                    executionResult = ExecutionResult(
                        scriptId = it.currentScript.id,
                        output = "脚本运行成功",
                        exitCode = 0,
                        duration = 1600L
                    )
                )
            }
        }
    }

    fun newScript() {
        _uiState.update {
            it.copy(
                currentScript = ScriptDocument(
                    id = UUID.randomUUID().toString(),
                    name = "untitled.${when (_uiState.value.selectedLanguage) {
                        ScriptLanguage.PYTHON -> "py"
                        ScriptLanguage.YAML -> "yaml"
                        ScriptLanguage.MARKDOWN -> "md"
                    }}",
                    language = _uiState.value.selectedLanguage
                ),
                executionResult = null,
                executionLogs = emptyList()
            )
        }
    }

    fun saveScript() {
        _uiState.update {
            val script = it.currentScript.copy(updatedAt = System.currentTimeMillis())
            it.copy(
                currentScript = script,
                scripts = (it.scripts.filter { s -> s.id != script.id } + script)
            )
        }
    }

    fun addVariable(name: String, placeholder: String, defaultValue: String) {
        _uiState.update {
            it.copy(promptVariables = it.promptVariables + PromptVariable(name, placeholder, defaultValue))
        }
    }

    fun removeVariable(name: String) {
        _uiState.update {
            it.copy(promptVariables = it.promptVariables.filter { v -> v.name != name })
        }
    }
}
