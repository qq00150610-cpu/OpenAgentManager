package com.openagent.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ScriptDocument(
    val id: String,
    val name: String,
    val content: String = "",
    val language: ScriptLanguage = ScriptLanguage.PYTHON,
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class ScriptLanguage {
    PYTHON, YAML, MARKDOWN
}

@Serializable
data class ExecutionResult(
    val scriptId: String,
    val output: String = "",
    val exitCode: Int = -1,
    val duration: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AgentTemplate(
    val id: String,
    val name: String,
    val description: String = "",
    val category: TemplateCategory = TemplateCategory.GENERAL,
    val codeSnippet: String = ""
)

@Serializable
enum class TemplateCategory {
    OPENCLAW_SKILL, HERMES_AGENT, PROMPT_TEMPLATE, GENERAL
}

@Serializable
data class PromptVariable(
    val name: String,
    val placeholder: String = "",
    val defaultValue: String = ""
)
