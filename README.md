# Freebuff Proxy 管理后台 (KMP)

基于 [freebuff-proxy](https://github.com/trefeon/freebuff-proxy) 后台管理功能的跨平台客户端，使用 Kotlin Multiplatform (KMP) + Compose Multiplatform 开发。

## 功能特性

### 🔗 连接管理
- 自定义服务器地址和端口
- 管理员认证
- 自动重连

### 📊 总览仪表盘
- 实时代理状态监控
- 请求统计（总量/今日/消息数）
- 令牌状态概览（活跃/空闲/冷却/封禁）
- 最近路由记录
- 一键 Smoke Test

### 🔑 令牌管理
- 令牌列表和详情
- 配额使用情况
- 信誉等级显示
- 单个/全部测试
- 解锁/结束令牌
- 添加/移除令牌
- 模式切换（Pooled/Bridge）

### 🤖 模型管理
- 可用模型列表
- 模型别名显示
- Agent 映射

### 🔍 请求追踪
- 请求链路追踪
- 阶段耗时分析
- 状态和错误详情

### 💬 聊天测试
- 多模型选择
- 实时对话测试
- 消息历史

### ⚙️ 配置管理
- 服务器配置编辑
- 认证设置
- 路由参数调整
- TLS & 安全配置
- 会话管理
- 限流设置
- 仪表盘配置
- 一键保存/重载

### 📋 部署指南
- 多客户端配置示例
  - OpenCode
  - Cursor / VS Code
  - Continue
  - Chatbox
  - cURL
- 连接信息展示

### 📝 日志查看
- 实时日志流
- 级别过滤（INFO/WARN/ERROR/DEBUG）
- 消息搜索
- 详情展开

### 📈 指标监控
- 实时统计数据
- 趋势分析
- 令牌级别明细
- Prometheus 配置示例

## 技术栈

- **Kotlin Multiplatform (KMP)** - 跨平台核心逻辑
- **Compose Multiplatform** - 声明式 UI
- **Ktor** - HTTP 客户端
- **Kotlinx Serialization** - JSON 序列化
- **Material 3** - 基础 UI 组件

## 设计语言

- **shadcn/ui** 风格：简洁、现代、克制
- **Apple Design** 元素：圆角卡片、模糊效果、系统色板
- **中文界面**：所有文本均为中文

## 构建

### Android

```bash
./gradlew :androidApp:assembleDebug
```

APK 输出路径：`androidApp/build/outputs/apk/debug/`

### CI/CD

推送到 `main` 分支或创建 `v*` 标签时，GitHub Actions 自动构建：

- Debug APK：每次推送
- Release APK：仅 `v*` 标签

## 配置

在 freebuff-proxy 的 `.env` 中配置：

```env
LISTEN_ADDR=:3457
ADMIN_TOKEN=your-admin-token
```

在 App 中输入：
- 服务器地址：`your-vps-ip`
- 端口：`3457`
- 管理密码：`your-admin-token`

## 目录结构

```
freebuff-admin-kmp/
├── androidApp/           # Android 应用
│   └── src/main/
│       ├── java/com/freebuff/admin/
│       │   └── MainActivity.kt
│       ├── AndroidManifest.xml
│       └── res/
├── shared/               # KMP 共享模块
│   └── src/commonMain/kotlin/com/freebuff/admin/
│       ├── api/          # API 客户端
│       ├── model/        # 数据模型
│       └── ui/           # UI 组件
│           ├── theme/    # 主题系统
│           ├── components/ # 通用组件
│           └── screens/  # 页面
├── .github/workflows/    # CI/CD
└── gradle/               # Gradle 配置
```

## License

MIT
