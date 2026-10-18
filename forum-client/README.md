# CodeBase 前端

基于 Vue 2、Vue Router、Vuex 和 Element UI 的编程社区前端，采用简约 GitHub 风格，支持桌面、平板和手机布局。完整的后端、MySQL 和 Redis 启动说明见[项目 README](../README.md#快速开始)。

## 本地开发

先启动后端，默认地址为 `http://localhost:8888`。在项目根目录执行：

```bash
cd forum-client
npm ci
npm run serve
```

访问 [http://localhost:8087](http://localhost:8087)。默认端口由 `vue.config.js` 的 `devServer.port` 指定，端口被占用时以终端实际输出为准。开发服务支持热更新；修改 `vue.config.js` 后需重启服务。

## 生产构建

在 `forum-client` 目录执行：

```bash
npm run build
```

产物位于 `dist/`。部署时需要配置实际后端地址，并为 Vue Router 的 history 模式设置回退到 `index.html`，使 `/source`、`/searchPage`、`/view/:id` 等路径可以直接访问。

## 界面与样式

- `src/assets/css/theme.scss`：公共主题入口，由 `src/main.js` 引入，统一配色、按钮、表单、卡片、弹窗及部分响应式样式。
- `src/assets/css/markdown.scss`：正文、预览及 AI 回答共用的浅色 Markdown 排版与代码块样式。
- `src/assets/css/admin.scss`：管理端列表、搜索栏、状态标签、资源审核与手机布局样式。
- `src/components/TheHeader.vue`：全站导航、移动端菜单、搜索与账号操作。
- `src/views/MainPage.vue`：首页多栏布局，以及手机端分类展开和社区看板抽屉。
- `src/components/article/ArticlePage.vue`：讨论列表、分页加载、空状态和失败重试。
- `src/components/card/SidebarArticles.vue`：热门讨论与推荐文章共用的侧栏列表。
- `src/components/article/MarkdownEditor.vue`：ByteMD 工作区，支持编辑、预览、桌面分屏、全屏、语法帮助及图片上传。
- `src/views/BlogWrite.vue`：讨论标题、分类标签、本地草稿自动保存 / 恢复和发布确认摘要。草稿按用户与文章区分，保存在当前浏览器。
- `src/components/aiModel/AiModel.vue`：AI 对话抽屉、快捷提问、Markdown 回复、停止生成、复制与重试。
- `src/views/Admin.vue`、`src/components/admin/`：管理导航、真实数据看板及讨论、评论、资源、成员管理。
- `src/utils/content.js`：将 HTML / Markdown 转换为纯文本和列表摘要。
- `src/utils/sse.js`：支持跨网络分块、多个 `data:` 行和 JSON 内容事件的 SSE 解析器。

6 张实际界面截图见[页面展示](../README.md#页面展示)。

## 后端连接

当前开发地址为本机 `localhost:8888`，更改地址时需检查以下入口：

| 文件 | 用途 |
|------|------|
| `src/api/http.js` | Axios 默认 API 地址 |
| `src/store/configure.js` | `HOST`，用于上传与资源地址等 |
| `src/assets/js/websocket.js` | 实时通知 WebSocket 地址 |
| `src/components/aiModel/AiModel.vue` | AI 流式请求地址 |

从其他设备访问或部署时，应将上述地址调整为浏览器可访问的后端地址。启用 GitHub 登录时，也需同步后端配置中的前端回跳地址和实际前端端口。

AI 接口 `/chat/ask` 通过 SSE 返回 `data: {"content":"文本片段"}` 事件，以 JSON 保留代码缩进和换行。前后端需一起更新；解析器也兼容旧接口的纯文本事件。
