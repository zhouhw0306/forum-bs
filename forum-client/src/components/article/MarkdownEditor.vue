<template>
  <div class="me-editor-wrapper" :class="{ 'theme-dark': prefersDark }">
    <!-- 编辑模式：ByteMD 分屏编辑+实时预览 -->
    <div
      v-if="isEditMode"
      class="bytemd-edit-container"
    >
      <Editor
        :value="editor.value"
        :plugins="plugins"
        :sanitize="sanitize"
        :uploadImages="handleUpload"
        :maxLength="50000"
        mode="split"
        placeholder="请输入 Markdown 内容..."
        @change="handleChange"
      />
    </div>
    <!-- 预览模式：ByteMD Viewer 纯渲染展示 -->
    <div v-else class="bytemd-view-container">
      <Viewer
        :value="editor.value"
        :plugins="plugins"
        :sanitize="sanitize"
      />
    </div>
  </div>
</template>

<script>
import { Editor, Viewer } from '@bytemd/vue'
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import math from '@bytemd/plugin-math'
import mermaid from '@bytemd/plugin-mermaid'
import mediumZoom from '@bytemd/plugin-medium-zoom'
import gemoji from '@bytemd/plugin-gemoji'
import { marked } from 'marked'

// ByteMD 样式
import 'bytemd/dist/index.css'
// 代码高亮主题
import 'highlight.js/styles/github-dark.min.css'
// KaTeX 数学公式样式
import 'katex/dist/katex.min.css'

export default {
  name: 'MarkdownEditor',
  components: { Editor, Viewer },
  props: {
    editor: Object
  },
  data() {
    return {
      plugins: [
        gfm(),
        highlight(),
        math(),
        mermaid(),
        mediumZoom(),
        gemoji()
      ],
      prefersDark: false
    }
  },
  computed: {
    // 兼容 BlogWrite(default_open) 和 BlogView(defaultOpen) 的属性名
    isEditMode() {
      const mode = this.editor.default_open !== undefined
        ? this.editor.default_open
        : this.editor.defaultOpen
      return mode !== 'preview'
    }
  },
  mounted() {
    // 预览模式不需要初始化编辑器
    if (!this.isEditMode) return

    // 检测系统暗色模式偏好
    this.prefersDark = window.matchMedia?.('(prefers-color-scheme: dark)').matches ?? false
    // 兼容旧代码：d_render getter，闭包捕获 editor
    const editor = this.editor
    this.$set(this.editor, 'ref', {
      get d_render() {
        return marked(editor.value || '', { breaks: true })
      }
    })
    // 监听暗色模式变化
    this._darkMq = window.matchMedia('(prefers-color-scheme: dark)')
    this._darkHandler = (e) => { this.prefersDark = e.matches }
    this._darkMq?.addEventListener('change', this._darkHandler)
  },
  beforeDestroy() {
    this._darkMq?.removeEventListener('change', this._darkHandler)
  },
  methods: {
    handleChange(v) {
      this.editor.value = v
    },
    sanitize(schema) {
      // ByteMD 默认会做 XSS 过滤，这里透传 schema 保留所有安全标签
      return schema
    },
    async handleUpload(files) {
      // 图片上传：支持拖拽和粘贴
      const uploadResults = []
      for (const file of files) {
        const formData = new FormData()
        formData.append('image', file)
        try {
          const { upload } = await import('@/api')
          const res = await upload(formData)
          if (res.data?.code === 0 && res.data.data?.url) {
            uploadResults.push({
              title: file.name,
              url: res.data.data.url
            })
          }
        } catch (e) {
          console.error('Image upload failed:', e)
        }
      }
      return uploadResults
    }
  }
}
</script>

<style>
/* ===== ByteMD 全局主题覆盖 ===== */

/* 亮色主题（默认）*/
.bytemd {
  height: 550px;
  border: none !important;
  border-radius: 0 !important;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
}

/* 工具栏 */
.bytemd-toolbar {
  background: #fafbfc !important;
  border-bottom: 1px solid #e1e4e8 !important;
  padding: 4px 12px !important;
}

.bytemd-toolbar-icon {
  border-radius: 4px;
}
.bytemd-toolbar-icon:hover {
  background: #e8eaed !important;
}

/* 编辑区 */
.CodeMirror {
  font-family: "JetBrains Mono", "Fira Code", "SF Mono", Consolas, monospace !important;
  font-size: 14px !important;
  line-height: 1.6 !important;
  padding: 16px 20px !important;
  background: #fefefe !important;
  color: #24292e !important;
}

/* 预览区 */
.bytemd-preview {
  padding: 16px 32px !important;
  background: #ffffff !important;
}

.markdown-body {
  font-size: 16px;
  line-height: 1.8;
  color: #24292e;
  max-width: 860px;
  margin: 0 auto;
}

/* 标题 */
.markdown-body h1 { font-size: 2em; border-bottom: 1px solid #eaecef; padding-bottom: .3em; margin: 24px 0 16px; }
.markdown-body h2 { font-size: 1.5em; border-bottom: 1px solid #eaecef; padding-bottom: .3em; margin: 24px 0 16px; }
.markdown-body h3 { font-size: 1.25em; margin: 24px 0 16px; }

/* 代码块——Mac 风格 + 复制按钮 */
.markdown-body pre {
  position: relative;
  border-radius: 8px;
  margin: 16px 0;
  padding: 0;
  overflow: hidden;
  background: #1e1e1e !important;
}

/* Mac 三色圆点 */
.markdown-body pre::before {
  content: '';
  position: absolute;
  top: 14px;
  left: 16px;
  width: 12px;
  height: 12px;
  background: #ff5f56;
  border-radius: 50%;
  box-shadow: 20px 0 #ffbd2e, 40px 0 #27c93f;
  z-index: 1;
}

.markdown-body pre code {
  display: block;
  padding: 40px 20px 16px 20px !important;
  overflow-x: auto;
  font-family: "JetBrains Mono", "Fira Code", "SF Mono", Consolas, monospace;
  font-size: 13.5px;
  line-height: 1.6;
  color: #d4d4d4;
  background: transparent;
}

/* 行内代码 */
.markdown-body :not(pre) > code {
  background: #f0f0f0;
  color: #e74c3c;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
}

/* 引用 */
.markdown-body blockquote {
  border-left: 4px solid #409EFF;
  padding: 8px 16px;
  margin: 16px 0;
  color: #6a737d;
  background: #f6f8fa;
  border-radius: 0 4px 4px 0;
}

/* 表格 */
.markdown-body table {
  border-collapse: collapse;
  width: 100%;
  margin: 16px 0;
}
.markdown-body th,
.markdown-body td {
  border: 1px solid #dfe2e5;
  padding: 8px 12px;
  text-align: left;
}
.markdown-body th {
  background: #f6f8fa;
  font-weight: 600;
}

/* 链接 */
.markdown-body a {
  color: #409EFF;
}

/* 图片 */
.markdown-body img {
  max-width: 100%;
  border-radius: 8px;
}

/* 代码块复制按钮（需要额外 JS 支持） */
.markdown-body pre .code-copy-btn {
  position: absolute;
  top: 8px;
  right: 12px;
  z-index: 2;
  padding: 4px 8px;
  font-size: 12px;
  color: #999;
  background: transparent;
  border: 1px solid #555;
  border-radius: 4px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}
.markdown-body pre:hover .code-copy-btn {
  opacity: 1;
}
.markdown-body pre .code-copy-btn:hover {
  color: #fff;
  border-color: #fff;
}

/* ===== 暗色模式 ===== */
.theme-dark .bytemd {
  background: #1e1e1e;
}
.theme-dark .bytemd-toolbar {
  background: #252526 !important;
  border-bottom-color: #3e3e42 !important;
}
.theme-dark .bytemd-toolbar-icon svg {
  fill: #cccccc;
}
.theme-dark .bytemd-toolbar-icon:hover {
  background: #3e3e42 !important;
}
.theme-dark .CodeMirror {
  background: #1e1e1e !important;
  color: #d4d4d4 !important;
}
.theme-dark .bytemd-preview {
  background: #1e1e1e !important;
}
.theme-dark .markdown-body {
  color: #d4d4d4;
}
.theme-dark .markdown-body h1,
.theme-dark .markdown-body h2 {
  border-bottom-color: #3e3e42;
}
.theme-dark .markdown-body :not(pre) > code {
  background: #333;
  color: #f48771;
}
.theme-dark .markdown-body blockquote {
  background: #252526;
  color: #aaa;
}

/* ===== 预览模式（BlogView 文章阅读）===== */
.bytemd-view-container {
  width: 100%;
  background: #fff;
  border-radius: 4px;
}
.bytemd-view-container .markdown-body {
  padding: 0;
}

.theme-dark .bytemd-view-container {
  background: #1e1e1e;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .bytemd {
    height: auto !important;
    min-height: 400px;
  }
  .bytemd-preview {
    padding: 12px 16px !important;
  }
  .CodeMirror {
    padding: 12px 16px !important;
    font-size: 13px !important;
  }
  .markdown-body {
    font-size: 15px;
  }
}
</style>
