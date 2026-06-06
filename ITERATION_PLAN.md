# 论坛项目迭代建议书

> 基于 2026-06-06 项目现状全面分析

---

## 一、项目现状总览

| 维度 | 技术栈 | 成熟度 | 风险 |
|------|--------|--------|------|
| 后端框架 | Spring Boot 2.7.1 (EOL) | ⚠️ | 不再接收安全补丁 |
| Java 版本 | JDK 1.8 | ⚠️ | 2022 年停止免费更新 |
| ORM | MyBatis-Plus 3.4.1 | ✅ | 稳定但可升级 |
| 前端框架 | Vue 2.6 (EOL) | ⚠️ | 2023 年底停止维护 |
| UI 库 | Element UI | ⚠️ | 与 Vue 2 绑定，无 Vue 3 版本 |
| 编辑器 | ByteMD | ✅ | 刚迁移完成 |
| 数据库 | MySQL 8.x | ✅ | 稳定 |
| 缓存 | Redis | ✅ | 仅用于 Spring Cache |
| 搜索 | Elasticsearch (禁用) | ❌ | 回退到 SQL LIKE |
| 消息推送 | WebSocket (javax) | ⚠️ | Spring Boot 3 已迁移至 jakarta |
| AI 对话 | 讯飞星火 WebSocket | ✅ | 可用但体验差 |
| 文件存储 | 七牛云 OSS | ✅ | 密钥硬编码在 yml |
| 测试 | 无 | ❌ | 没有单元测试或集成测试 |

---

## 二、P0 — 安全与稳定性（必须尽快修复）

### 2.1 密钥和敏感信息管理

**现状**：[application.yml](forum-server/src/main/resources/application.yml) 中明文存储数据库密码、Redis 密码、邮箱密码、七牛云 AK/SK、讯飞 API 密钥、GitHub OAuth secret。

**建议**：
- 引入环境变量或配置中心（Nacos/Apollo），所有密钥通过 `${ENV_VAR}` 注入
- 或短期方案：使用 `jasypt-spring-boot-starter` 加密敏感字段
- 将 `application.yml` 从 git 历史中移除敏感值（使用 `git filter-branch`）

### 2.2 JWT 安全增强

**现状**：
- token 存储在请求头 `token` 字段，无过期刷新机制
- [JwtInterceptor](forum-server/src/main/java/com/example/config/interceptor/JwtInterceptor.java) 只验证 token 有效性
- 无 token 黑名单（用户登出后 token 仍可用）

**建议**：
- 实现 Access Token + Refresh Token 双 token 机制（AT 15min / RT 7day）
- 登出时将 token 加入 Redis 黑名单（TTL 与 token 剩余有效期一致）
- 增加 token 自动续期（AT 过期前 5 分钟自动刷新）
- 存储用户设备指纹（IP + User-Agent），异常时强制重新登录

### 2.3 输入校验与 XSS 防护

**现状**：ByteMD 前端有默认 XSS 过滤，但后端未对存储的 HTML 内容做二次过滤。

**建议**：
- 后端入库前使用 `jsoup` 或 OWASP HTML Sanitizer 对 `contentHtml` 做白名单过滤
- 对 Markdown 原始内容做敏感词过滤（已有 `SensitiveFilter`，需确认覆盖面和性能）
- 添加请求参数校验（已有 `@Valid`，需确保所有接口覆盖）

### 2.4 SQL 注入防护

**现状**：MyBatis-Plus 使用参数化查询，但需检查自定义 XML Mapper 中是否存在 `${}` 拼接。

**建议**：全局搜索 `${}` → 全部替换为 `#{}`。

### 2.5 升级到 Spring Boot 3.x / Java 17+

**收益**：
- 安全补丁支持
- 虚拟线程（Java 21）大幅提升 WebSocket/异步场景吞吐
- GraalVM Native Image 编译支持
- Jakarta EE 迁移（WebSocket API 从 javax → jakarta）

**风险**：javax → jakarta 包名变更影响 WebSocket、Servlet Filter 等。

---

## 三、P1 — 功能增强（提升用户体验）

### 3.1 搜索功能修复与增强

**现状**：Elasticsearch 在配置中 `enabled: false`，[ArticleSearchRepository](forum-server/src/main/java/com/example/search/ArticleSearchRepository.java) 存在但未激活，搜索回退到 SQL `LIKE`。

**建议**：
- 启用 ES 或切换到 **Meilisearch**（更轻量，中文支持好）
- 实现全站搜索（文章标题 + 内容 + 标签 + 分类）
- 搜索建议/自动补全
- 搜索结果高亮
- 搜索历史记录

### 3.2 通知系统完善

**现状**：[WebSocketComponent](forum-server/src/main/java/com/example/websocket/WebSocketComponent.java) 已建立 WebSocket 通道，[NotificationController](forum-server/src/main/java/com/example/controller/NotificationController.java) 和 [NotificationMessage](forum-server/src/main/java/com/example/domain/vo/NotificationMessage.java) 已存在。

**缺失**：
- 通知持久化（目前通知只是即时推送，刷新页面后丢失）
- 通知类型不全（评论回复、点赞、关注、系统通知等）
- 无「全部已读」「清空通知」功能
- 前端无通知中心 UI

**建议**：
- 通知落库（tb_notification 表），`is_read` 字段标记已读/未读
- 通知类型枚举：`COMMENT` / `REPLY` / `LIKE` / `FAVOUR` / `FOLLOW` / `SYSTEM`
- WebSocket 推送 + 入库双写
- API：未读数量、列表（分页）、标记已读、全部已读、删除
- 顶部导航栏增加🔔通知图标 + 未读红点

### 3.3 评论系统升级

**现状**：只支持一级评论 + 回复，无嵌套。

**建议**：
- 支持二层嵌套评论（评论 → 回复），超过二层折叠
- 评论支持 Markdown 语法（复用 ByteMD Viewer 渲染）
- 评论点赞/踩
- 评论排序（按时间/按热度）
- @提及用户功能

### 3.4 用户体系完善

**缺失功能**：
- 用户个人主页（展示文章列表、收藏、关注数、积分）
- 用户等级/徽章体系（基于积分、发帖数、获赞数）
- 用户头像裁剪/上传
- 修改密码、绑定邮箱
- 账号注销

### 3.5 内容审核与举报

**建议**：
- 文章/评论举报入口
- 管理员审核后台（已有 Admin 页面，需增强审核流）
- 自动敏感词 + AI 内容审核
- 违规内容自动隐藏 + 手动复核

---

## 四、P2 — 架构升级（长期竞争力）

### 4.1 前端升级到 Vue 3 + Element Plus

**背景**：Vue 2 已于 2023 年底停止维护，Element UI 无 Vue 3 版本。

**迁移路径**：
```
Vue 2.6 + Element UI + Vuex
        ↓
Vue 3.4 + Element Plus + Pinia + TypeScript
```

| 组件 | 旧 | 新 | 说明 |
|------|-----|-----|------|
| 框架 | Vue 2.6 | Vue 3.4+ | Composition API |
| UI 库 | Element UI | Element Plus | API 高度兼容 |
| 状态管理 | Vuex | Pinia | 官方推荐，TS 友好 |
| 类型系统 | 无 | TypeScript | 减少运行时 bug |
| 构建工具 | Vue CLI 5 | Vite 5 | 热更新秒级 |
| 编辑器 | ByteMD | ByteMD (@bytemd/vue-next) | 有 Vue 3 版本 |

### 4.2 引入 TypeScript

**收益**：
- 编译时类型检查，减少低级错误
- API 接口类型定义 → 全端类型共享
- IDE 智能提示和重构能力

**建议**：后端也逐步迁移（Lombok → 显式编码或 Java Record）

### 4.3 数据库优化

**现状问题**：
- `tb_article.content` 和 `tb_article.content_html` 同时存储，数据冗余
- 无全文索引（搜索走 LIKE）
- 无读写分离

**建议**：
- 大文本字段分离到独立表（垂直分表），减少主表扫描
- 热点文章缓存到 Redis（读多写少场景）
- 引入 Canal + MQ 做缓存一致性
- 定期归档旧数据

### 4.4 测试体系建立

**现状**：零测试。

**建议**：
```
分层测试：
├── 单元测试（JUnit 5 + Mockito）覆盖 Service 层
├── 集成测试（@SpringBootTest）覆盖 Mapper + Controller
├── API 测试（REST Assured / JMeter）
├── 前端测试（Vitest + Vue Test Utils）
└── E2E 测试（Playwright / Cypress）
```

### 4.5 DevOps / CI/CD

**建议**：
- Docker Compose 一键启动（MySQL + Redis + ES + App）
- GitHub Actions / Jenkins 自动构建 + 测试 + 部署
- 引入 Flyway/Liquibase 做数据库版本管理
- 日志收集（ELK/Loki）

---

## 五、P3 — 创新功能（差异化竞争）

### 5.1 AI 能力增强

**现状**：仅支持讯飞星火单轮对话。

**建议**：
- 多模型支持（OpenAI / Claude / DeepSeek / 本地模型），抽象 LLM Provider 接口
- **AI 辅助写作**：AI 续写、摘要生成、标题优化、错别字检查
- **AI 代码审查**：贴代码 → AI 点评优化建议
- **智能标签推荐**：根据文章内容自动推荐分类和标签
- AI 生成文章封面图（DALL·E / Stable Diffusion）

### 5.2 社区互动增强

- **积分/经验值系统**：发帖 +N、被点赞 +N、被收藏 +N、每日签到 +N
- **排行榜**：周榜/月榜（发帖数、被赞数、积分）
- **成就徽章**：首批用户、优质作者、代码达人、日更挑战
- **话题/专栏**：类似 GitHub Discussions 的分类讨论区

### 5.3 代码协作功能

- **在线代码运行**（Judge0 / Piston API）
- **代码 Diff 对比**（贴两段代码显示差异）
- **GitHub Gist 嵌入**（编辑器插入 Gist 卡片）
- **LeetCode 题目卡片嵌入**

### 5.4 移动端适配

**建议**：
- 响应式优化（ByteMD 已做基础适配）
- 或 PWA 渐进式 Web App（离线访问 + 推送通知）
- 或 Flutter/React Native 原生 App（长期）

### 5.5 国际化 (i18n)

- vue-i18n 前端多语言
- 后端异常信息国际化
- 首期：中文 + 英文

---

## 六、迭代路线图建议

```
Phase 1 (当前 ~ 2026.Q3) — 安全加固 & 体验优化
├── 密钥管理（环境变量/jasypt）
├── 搜索功能启用（Meilisearch）
├── 通知系统完善（落库 + 前端 UI）
├── 评论二层嵌套
├── Docker Compose 开发环境
└── 后端单元测试（覆盖率 > 40%）

Phase 2 (2026.Q4) — 架构升级
├── Spring Boot 3.x + Java 17 迁移
├── Vue 3 + Element Plus + TypeScript 重构
├── Vite 构建替换 Vue CLI
├── Pinia 状态管理
├── CI/CD Pipeline (GitHub Actions)
└── 后端集成测试

Phase 3 (2027.Q1) — AI & 社区深化
├── AI 多模型支持
├── AI 辅助写作 & 代码审查
├── 积分 & 成就系统
├── 移动端 PWA
└── E2E 测试

Phase 4 (2027.Q2+) — 生态建设
├── 插件系统（编辑器插件/主题市场）
├── 国际化 (i18n)
├── 多租户/团队版
└── 开源社区运营
```

---

## 七、技术债清单

| 序号 | 问题 | 严重程度 | 影响范围 | 建议 |
|------|------|----------|----------|------|
| 1 | 密钥明文存储 | 🔴 致命 | 全部服务 | jasypt + 环境变量 |
| 2 | 无测试 | 🔴 致命 | 质量保障 | 至少 Service 层测试 |
| 3 | ES 搜索禁用 | 🟡 高 | 搜索体验 | 启用 ES 或 Meilisearch |
| 4 | Java 8 EOL | 🟡 高 | 安全补丁 | 升级 Java 17+ |
| 5 | Spring Boot 2.7 EOL | 🟡 高 | 安全补丁 | 升级 3.x |
| 6 | Vue 2 EOL | 🟡 高 | 前端维护 | 迁移 Vue 3 |
| 7 | 通知不持久化 | 🟡 高 | 用户体验 | 落库 + 前端 UI |
| 8 | Token 无刷新机制 | 🟡 高 | 安全/体验 | Access + Refresh Token |
| 9 | 无读写分离 | 🟢 中 | 性能 | 主从 + Redis 缓存 |
| 10 | 无 CI/CD | 🟢 中 | 开发效率 | GitHub Actions |
| 11 | 无 API 文档在线版 | 🟢 低 | 开发体验 | Knife4j 已有，部署到公共 URL |
| 12 | 前端无 TS | 🟢 低 | 代码质量 | Vue 3 迁移时引入 |
| 13 | 重复提交防护不完整 | 🟢 低 | 数据一致性 | 全局幂等性注解 |

---

## 八、总结

该项目是一个功能较为完整的程序员技术论坛，具备文章发布、评论互动、资源分享、AI 对话、实时通知等核心功能。当前最大的风险在于：

1. **安全**：密钥泄露、JWT 无刷新机制
2. **可维护性**：Java 8 + Vue 2 均已 EOL，升级迫在眉睫
3. **体验**：搜索功能残缺、通知不持久化

建议先集中 1-2 个月完成 Phase 1（安全加固 + 体验优化），再启动 Phase 2 的架构升级。每次迭代保持可发布状态，避免大爆炸式重构。
