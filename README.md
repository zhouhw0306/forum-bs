<p align="center">
  <h1 align="center">💬 CodeBase 编程社区</h1>
  <p align="center">一个面向程序员的轻量级技术论坛 —— 写作、分享、讨论、AI 辅助</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.1-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-2.6-blue" alt="Vue">
  <img src="https://img.shields.io/badge/Java-1.8-orange" alt="Java">
  <img src="https://img.shields.io/badge/MyBatis%20Plus-3.4.1-red" alt="MyBatis Plus">
  <img src="https://img.shields.io/badge/Element%20UI-2.15-lightblue" alt="Element UI">
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License">
</p>

---

## 📖 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [页面展示](#页面展示)
- [迭代计划](#迭代计划)
- [贡献指南](#贡献指南)

---

## 项目简介

CodeBase 是一个专为程序员打造的技术社区平台，支持 Markdown 写作、代码高亮、AI 对话、资源分享等功能。用户可以发布技术文章、评论互动、收藏点赞，体验流畅的编程技术交流。

---

## 功能特性

### 📝 内容创作
- **Markdown 编辑器** — 基于 ByteMD，支持 GFM 表格、数学公式、Mermaid 图表、流程图
- **代码高亮** — Highlight.js 自动语法着色
- **图片上传** — 拖拽/粘贴上传，七牛云 OSS 托管

### 💬 社区互动
- 文章发布、编辑、删除
- 评论 / 回复
- 点赞、收藏、关注
- 资源分享（Source 模块）

### 🤖 AI 能力
- 集成**讯飞星火大模型**，在线 Chat AI 对话
- 支持多轮对话上下文

### 🔍 智能推荐
- 基于用户的**协同过滤推荐算法**（UserCF）
- 个性化首页内容推荐

### 🔔 实时通知
- WebSocket 实时消息推送
- 评论、回复、点赞等互动通知

### 🛡️ 安全与风控
- **JWT** 登录认证
- **AOP + Redis** 接口权限校验、限流、防重复提交
- **前缀树（Trie）** 敏感词过滤
- GitHub OAuth 2.0 第三方登录

### 🖥️ 后台管理
- 用户管理
- 文章管理
- 分类/标签管理
- 资源管理
- 数据统计面板（ECharts）

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 2.7.1 |
| **语言** | Java | 1.8 |
| **ORM** | MyBatis-Plus | 3.4.1 |
| **数据库** | MySQL | 8.x |
| **缓存** | Redis | — |
| **搜索引擎** | Elasticsearch | — |
| **安全** | JWT + 自定义注解 | — |
| **前端框架** | Vue | 2.6 |
| **UI 组件库** | Element UI | 2.15 |
| **状态管理** | Vuex | 3.6 |
| **路由** | Vue Router | 3.5 |
| **Markdown 编辑器** | ByteMD | 1.22 |
| **图表** | ECharts + v-charts | 4.9 |
| **构建工具** | Vue CLI | 5.0 |
| **CSS 预处理** | Sass | — |

---

## 项目结构

```
forum-bs/
├── forum-server/                   # 后端 Spring Boot
│   ├── src/main/java/com/example/
│   │   ├── annotation/             # 自定义注解（限流、权限）
│   │   ├── aop/                    # AOP 切面（限流、重复提交）
│   │   ├── config/                 # 配置类（拦截器、MyBatis-Plus、WebSocket）
│   │   │   └── interceptor/        # JWT 拦截器
│   │   ├── constant/               # 常量 & 枚举
│   │   ├── controller/             # 控制器
│   │   │   ├── article/            # 文章相关
│   │   │   ├── chat/               # AI 对话（星火 WebSocket）
│   │   │   ├── source/             # 资源分享
│   │   │   ├── type/               # 分类 & 标签
│   │   │   └── user/               # 用户相关
│   │   ├── domain/                 # 实体类
│   │   │   ├── bo/                 # 业务对象
│   │   │   ├── dao/                # 数据访问对象
│   │   │   ├── dto/                # 数据传输对象
│   │   │   └── vo/                 # 视图对象
│   │   ├── event/                  # Spring Event 事件机制
│   │   ├── exception/              # 全局异常处理
│   │   ├── filter/                 # 过滤器（敏感词）
│   │   ├── handler/                # 处理器
│   │   ├── mapper/                 # MyBatis Mapper 接口
│   │   ├── search/                 # Elasticsearch Repository
│   │   ├── service/                # 业务层
│   │   │   └── impl/               # 业务实现
│   │   ├── utils/                  # 工具类（JWT、协同过滤）
│   │   └── websocket/              # WebSocket 组件
│   └── src/main/resources/
│       ├── application.yml         # 应用配置
│       └── mapper/                 # MyBatis XML
│
├── forum-client/                   # 前端 Vue 2
│   ├── src/
│   │   ├── api/                    # API 接口封装
│   │   ├── assets/                 # 静态资源
│   │   │   ├── css/                # 样式
│   │   │   ├── data/               # 本地数据
│   │   │   ├── img/                # 图片
│   │   │   └── js/                 # 工具脚本（WebSocket 客户端）
│   │   ├── components/             # 公共组件
│   │   │   ├── admin/              # 后台管理组件
│   │   │   ├── aiModel/            # AI 对话组件
│   │   │   ├── article/            # 文章/编辑器组件
│   │   │   ├── card/               # 卡片组件
│   │   │   ├── gotop/              # 回到顶部
│   │   │   ├── source/             # 资源组件
│   │   │   └── table/              # 表格组件
│   │   ├── mixins/                 # Vue 混入
│   │   ├── router/                 # 路由配置
│   │   ├── store/                  # Vuex 状态管理
│   │   └── views/                  # 页面视图
│   ├── package.json
│   └── vue.config.js
│
└── ITERATION_PLAN.md               # 迭代计划书
```

---

## 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 1.8+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Maven | 3.6+ |
| Node.js | 18.19+ |
| npm | 10.2+ |

### 1. 克隆仓库

```bash
git clone https://github.com/zhouhw0306/forum-bs.git
cd forum-bs
```

### 2. 初始化数据库

创建 MySQL 数据库并导入初始化脚本（如有），默认数据库名：`forum`

### 3. 启动后端

```bash
cd forum-server
# 修改 src/main/resources/application.yml 中的数据库、Redis 连接信息
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8088`

### 4. 启动前端

```bash
cd forum-client
npm install
npm run serve
```

前端默认运行在 `http://localhost:8080`

---

## 配置说明

`application.yml` 中需要配置以下关键项：

| 配置项 | 说明 |
|--------|------|
| `spring.datasource` | MySQL 数据库连接 |
| `spring.redis` | Redis 连接 |
| `spring.mail` | 邮箱服务（用于验证码） |
| `qiniu` | 七牛云 OSS（AccessKey / SecretKey） |
| `xfXh` | 讯飞星火大模型 API |
| `github` | GitHub OAuth App 凭证 |
| `elasticsearch` | ES 连接（当前默认关闭） |

> ⚠️ **安全提醒**：建议使用环境变量 `${ENV_VAR}` 注入敏感配置，避免密钥泄露。

---

## 页面展示

| 首页 | AI 对话 |
|------|---------|
| ![首页](https://img-blog.csdnimg.cn/c7cccb6f7ccc4540b378e93eb268ccda.png) | ![AI对话](https://img-blog.csdnimg.cn/3dbe0061da124a5095ecb64fa5973aa4.png) |

| 文章详情 | 后台管理 |
|----------|----------|
| ![文章](https://img-blog.csdnimg.cn/81bd374023904c19b705908c04a432a9.png) | ![后台](https://img-blog.csdnimg.cn/a3d03193a79c452e82165aa6dc7bd490.png) |

---

## 迭代计划

项目的详细迭代计划见 [ITERATION_PLAN.md](./ITERATION_PLAN.md)，核心路线：

```
Phase 1 (2026.Q3)  — 安全加固 & 体验优化
Phase 2 (2026.Q4)  — 架构升级（Spring Boot 3.x + Vue 3 + TS）
Phase 3 (2027.Q1)  — AI & 社区深化
Phase 4 (2027.Q2+) — 生态建设
```

---

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建功能分支：`git checkout -b feature/AmazingFeature`
3. 提交更改：`git commit -m 'Add some AmazingFeature'`
4. 推送到分支：`git push origin feature/AmazingFeature`
5. 提交 Pull Request

---

## 📄 License

MIT © [zhouhw0306](https://github.com/zhouhw0306)
