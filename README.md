# OpenAgent Manager

基于 OpenClaw + Hermes Agent 架构的统一安卓 UI 管理界面。

## 项目概述

本项目是一个完整的 Android 应用，用于管理和监控 AI Agent 系统。支持 OpenClaw 和 Hermes Agent 两大平台的统一管理。

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose + Material 3
- **依赖注入**: Hilt (Dagger)
- **网络**: OkHttp (HTTP + WebSocket)
- **异步**: Kotlin Coroutines + Flow
- **本地存储**: DataStore + Room
- **导航**: Navigation Compose
- **序列化**: Kotlinx Serialization

## 项目结构

```
OpenAgentManager/
├── app/                          # 主壳工程
│   └── src/main/java/
│       └── com/openagent/manager/
│           ├── MainActivity.kt
│           ├── OpenAgentApplication.kt
│           ├── di/               # 依赖注入模块
│           └── ui/               # 主界面
│               ├── navigation/   # 导航图
│               ├── dashboard/    # 仪表盘
│               ├── agents/       # Agent 管理
│               ├── editor/       # Agent 编辑器
│               └── settings/     # 设置
├── core/
│   ├── model/                    # 数据模型
│   ├── network/                  # 网络层 (OpenClaw WebSocket + Hermes REST)
│   └── ui/                       # 共享 UI 组件和主题
├── feature/
│   ├── openclaw/                 # OpenClaw 功能模块
│   ├── openhermes/               # Hermes 功能模块
│   └── agent-editor/             # Agent 编辑器功能
└── gradle/
```

## 功能模块

### 1. 仪表盘 (Dashboard)
- OpenClaw / Hermes 连接状态卡片
- 实时统计（活跃会话、消息吞吐量、网关延迟等）
- 实时日志查看器（支持级别筛选和关键字搜索）
- 快捷操作（启动 Agent、切换模型、健康诊断）

### 2. Agents 管理
- Agent 列表（支持搜索和类型筛选）
- Agent 详情页（配置、能力、会话列表）
- OpenClaw 特有：Skills 市场、节点管理、渠道配置
- Hermes 特有：模型管理、成本分析、多平台渠道

### 3. Agent 编辑器
- 分屏模式：代码编辑器 + 运行控制台
- 多语言支持（Python、YAML、Markdown）
- 模板库（OpenClaw Skills、Hermes Agent、提示词模板）
- 变量面板（支持 `{{variable}}` 插值）
- 脚本运行与调试

### 4. 设置
- API 连接配置
- QR 码 / mDNS 自动发现
- 主题与显示（深色/浅色、语言切换）
- 安全设置（生物识别锁、数据加密）
- 本地存储管理

## 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 35
- Gradle 8.11+

### 构建步骤

1. 使用 Android Studio 打开项目根目录
2. 等待 Gradle 同步完成
3. 选择目标设备或模拟器
4. 点击 Run 运行

### 命令行构建

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本
./gradlew assembleRelease
```

## 配置说明

### OpenClaw Gateway 连接

在设置页面或仪表盘中配置：
- **Gateway URL**: `wss://your-gateway.com` 或 `ws://localhost:3000`
- **认证 Token**: 从 OpenClaw Gateway 获取

### Hermes Agent 连接

- **后端地址**: `http://your-hermes-server:8080`
- **API Key**: 从 Hermes Agent 获取
- **API 模式**: Chat Completions / Codex Responses / Anthropic Messages

## 开发说明

### 添加新功能模块

1. 在 `feature/` 下创建新模块
2. 添加 `build.gradle.kts` 配置
3. 在 `settings.gradle.kts` 中注册模块
4. 在 `app/build.gradle.kts` 中添加依赖

### 数据模型扩展

在 `core/model/` 中定义新的 data class，使用 `@Serializable` 注解。

### 网络层扩展

- OpenClaw: 在 `core/network/openclaw/` 中扩展 WebSocket 帧处理
- Hermes: 在 `core/network/hermes/` 中添加新的 API 端点

## 许可证

MIT License
