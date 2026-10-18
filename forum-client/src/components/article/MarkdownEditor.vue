<template>
  <div class="me-editor-wrapper">
    <section v-if="isEditMode" class="markdown-workspace" :class="['is-' + view, { 'is-fullscreen': fullscreen }]">
      <header class="editor-view-bar">
        <div class="editor-view-tabs" role="tablist" aria-label="编辑器视图"><button v-for="tab in viewTabs" :key="tab.value" :class="{ active: view === tab.value }" role="tab" :aria-selected="String(view === tab.value)" @click="setView(tab.value)"><i :class="tab.icon"></i>{{ tab.label }}</button></div>
        <div class="editor-utilities"><button :class="{ active: showHelp }" @click="showHelp = !showHelp" aria-label="Markdown 语法帮助" title="Markdown 语法帮助"><i class="el-icon-question"></i></button><button @click="toggleFullscreen" :aria-label="fullscreen ? '退出编辑器全屏' : '全屏编辑'" :title="fullscreen ? '退出全屏' : '全屏编辑'"><i :class="fullscreen ? 'el-icon-close' : 'el-icon-full-screen'"></i></button></div>
      </header>
      <div v-if="showHelp" class="markdown-cheatsheet"><div><code># 标题</code><span>标题</span></div><div><code>**重点**</code><span>加粗</span></div><div><code>- 项目</code><span>列表</span></div><div><code>[描述](链接)</code><span>链接</span></div><div><code>{{ codeFence }}js</code><span>代码块</span></div><div><kbd>Ctrl / ⌘ + B</kbd><span>加粗选区</span></div></div>
      <div class="editor-column-labels" v-if="view === 'split'"><span>Markdown</span><span>实时预览</span></div>
      <div v-show="view !== 'preview'" class="editor-canvas"><Editor ref="bytemd" :value="editor.value" :plugins="plugins" :locale="locale" :sanitize="sanitize" :uploadImages="handleUpload" :maxLength="50000" :editorConfig="editorConfig" mode="split" placeholder="从这里开始写作…支持 Markdown、代码块和图片。" @change="handleChange" /></div>
      <div v-if="view === 'preview'" class="editor-preview"><Viewer v-if="editor.value.trim()" :value="editor.value" :plugins="plugins" :sanitize="sanitize" /><div v-else class="editor-empty"><i class="el-icon-document"></i><h3>你的内容，会在这里呈现。</h3><p>切换到编辑，写下第一行内容。</p><button @click="setView('write')">开始写作 <i class="el-icon-right"></i></button></div></div>
      <footer class="editor-status"><span><i class="el-icon-edit-outline"></i> Markdown <span class="editor-image-hint">· 可拖拽或粘贴图片</span></span><span>{{ editor.value.length.toLocaleString() }} / 50,000 字</span></footer>
    </section>
    <div v-else class="bytemd-view-container"><Viewer :value="editor.value" :plugins="plugins" :sanitize="sanitize" /></div>
  </div>
</template>

<script>
import { Editor, Viewer } from '@bytemd/vue'
import { getProcessor } from 'bytemd'
import locale from 'bytemd/locales/zh_Hans.json'
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import math from '@bytemd/plugin-math'
import mermaid from '@bytemd/plugin-mermaid'
import mediumZoom from '@bytemd/plugin-medium-zoom'
import gemoji from '@bytemd/plugin-gemoji'
import 'bytemd/dist/index.css'
import 'katex/dist/katex.min.css'

export default {
  name: 'MarkdownEditor', components: { Editor, Viewer },
  props: { editor: { type: Object, required: true } },
  data() {
    return {
      locale, view: window.innerWidth > 900 ? 'split' : 'write', wide: window.innerWidth > 900, fullscreen: false, showHelp: false,
      codeFence: String.fromCharCode(96).repeat(3),
      editorConfig: { lineWrapping: true, tabSize: 2, indentUnit: 2, autofocus: false },
      plugins: [gfm(), highlight(), math(), mermaid(), mediumZoom(), gemoji(), { editorEffect: ({ editor }) => { this._codeEditor = editor; return () => { this._codeEditor = null } } }]
    }
  },
  computed: {
    isEditMode() { return (this.editor.default_open !== undefined ? this.editor.default_open : this.editor.defaultOpen) !== 'preview' },
    viewTabs() { return [{ value: 'write', label: '编辑', icon: 'el-icon-edit-outline' }, { value: 'preview', label: '预览', icon: 'el-icon-view' }].concat(this.wide ? [{ value: 'split', label: '分屏', icon: 'el-icon-copy-document' }] : []) }
  },
  mounted() {
    if (!this.isEditMode) return
    const editor = this.editor
    const plugins = this.plugins
    this.$set(editor, 'ref', { get d_render() { return String(getProcessor({ plugins }).processSync(editor.value || '')) } })
    window.addEventListener('resize', this.onResize)
    window.addEventListener('keydown', this.onKeydown)
  },
  beforeDestroy() { window.removeEventListener('resize', this.onResize); window.removeEventListener('keydown', this.onKeydown); this.$refs.bytemd?.editor?.$destroy() },
  methods: {
    handleChange(value) { this.editor.value = value },
    sanitize(schema) { return schema },
    setView(value) { this.view = value; this.$nextTick(() => { this._codeEditor?.refresh() }) },
    toggleFullscreen() { this.fullscreen = !this.fullscreen; this.$nextTick(() => { this._codeEditor?.refresh() }) },
    onResize() { this.wide = window.innerWidth > 900; if (!this.wide && this.view === 'split') this.setView('write'); this.$nextTick(() => { this._codeEditor?.refresh() }) },
    onKeydown(event) { if (event.key === 'Escape' && this.fullscreen) this.toggleFullscreen() },
    async handleUpload(files) {
      const results = []
      const { upload } = await import('@/api')
      for (const file of files) {
        const formData = new FormData(); formData.append('image', file)
        try {
          const response = await upload(formData)
          if (response.data?.code !== 0 || !response.data.data?.url) throw new Error('Upload failed')
          results.push({ title: file.name, url: response.data.data.url })
        } catch (error) { this.$message.error('图片上传失败，请检查上传服务后重试') }
      }
      return results
    }
  }
}
</script>

<style scoped>
.me-editor-wrapper { min-width: 0; }
.markdown-workspace { border: 1px solid var(--border); border-radius: 6px; overflow: hidden; background: var(--surface); min-width: 0; }
.editor-view-bar { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 0 12px; background: var(--surface-muted); border-bottom: 1px solid var(--border); min-height: 46px; }
.editor-view-tabs { display: flex; align-self: stretch; gap: 4px; }
.editor-view-tabs button { display: flex; align-items: center; gap: 6px; position: relative; padding: 0 13px; border: 0; border-bottom: 2px solid transparent; background: transparent; color: var(--text-muted); cursor: pointer; font-size: 12px; }
.editor-view-tabs button.active { color: var(--text); font-weight: 600; background: var(--surface); border-bottom-color: #fd8c73; } .editor-view-tabs button:hover { color: var(--text); }
.editor-utilities { display: flex; align-items: center; gap: 4px; } .editor-utilities button { display: grid; place-items: center; width: 28px; height: 28px; border: 0; background: transparent; border-radius: 4px; color: var(--text-muted); font-size: 16px; cursor: pointer; } .editor-utilities button:hover, .editor-utilities button.active { background: var(--hover); color: var(--text); }
.editor-column-labels { display: grid; grid-template-columns: 1fr 1fr; padding: 9px 0; border-bottom: 1px solid var(--border-subtle); font-size: 10px; color: var(--text-muted); background: var(--surface); } .editor-column-labels span { padding: 0 20px; }
.editor-canvas >>> .bytemd { display: flex; flex-direction: column; height: 480px; border: 0; border-radius: 0; background: var(--surface); color: var(--text); }
.editor-canvas >>> .bytemd-toolbar { flex-shrink: 0; min-height: 40px; padding: 6px 12px; border-bottom: 1px solid var(--border-subtle); background: var(--surface); overflow: visible; }
.editor-canvas >>> .bytemd-toolbar-left { display: flex; flex-wrap: wrap; align-items: center; gap: 2px; float: none; }
.editor-canvas >>> .bytemd-toolbar-icon { margin: 0; border-radius: 4px; color: var(--text-muted); } .editor-canvas >>> .bytemd-toolbar-icon svg { width: 28px; height: 28px; padding: 6px; } .editor-canvas >>> .bytemd-toolbar-icon:hover { color: var(--text); background: var(--hover); }
.editor-canvas >>> .bytemd-toolbar-right, .editor-canvas >>> .bytemd-toolbar-tab, .editor-canvas >>> .bytemd-status { display: none; }
.editor-canvas >>> .bytemd-body { display: flex; flex: 1; height: auto; min-height: 0; overflow: hidden; }
.editor-canvas >>> .bytemd-editor, .editor-canvas >>> .bytemd-preview { min-width: 0; height: 100%; }
.editor-canvas >>> .CodeMirror { height: 100%; padding: 0; font-family: ui-monospace, SFMono-Regular, Consolas, monospace; font-size: 13px; line-height: 1.8; background: var(--surface); color: var(--text); }
.editor-canvas >>> .CodeMirror-lines { padding: 20px 0; } .editor-canvas >>> .CodeMirror pre.CodeMirror-line, .editor-canvas >>> .CodeMirror pre.CodeMirror-line-like { padding: 0 20px; }
.editor-canvas >>> .bytemd-preview { background: var(--surface); border-left: 1px solid var(--border-subtle); } .editor-canvas >>> .bytemd-preview .markdown-body { padding: 20px; font-size: 14px; }
.is-write .editor-canvas >>> .bytemd-preview { display: none; } .is-write .editor-canvas >>> .bytemd-editor { width: 100% !important; }
.editor-preview { height: 480px; overflow-y: auto; padding: 28px; } .editor-preview >>> .markdown-body { max-width: 820px; margin: auto; }
.editor-empty { display: flex; align-items: center; justify-content: center; flex-direction: column; min-height: 360px; text-align: center; color: var(--text-muted); } .editor-empty > i { font-size: 34px; margin-bottom: 20px; color: #8c959f; } .editor-empty h3 { font-size: 15px; font-weight: 500; color: var(--text); margin-bottom: 8px; } .editor-empty p { font-size: 12px; } .editor-empty button { color: var(--accent); background: none; border: 0; cursor: pointer; margin-top: 16px; font-size: 12px; }
.editor-status { display: flex; align-items: center; justify-content: space-between; gap: 10px; padding: 11px 16px; border-top: 1px solid var(--border-subtle); background: var(--surface-muted); color: var(--text-muted); font-size: 10px; } .editor-status > span:first-child { display: flex; align-items: center; gap: 5px; }
.markdown-cheatsheet { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 12px; padding: 16px 20px; border-bottom: 1px solid var(--border); background: #f0f7ff; }
.markdown-cheatsheet > div { display: flex; flex-direction: column; gap: 5px; min-width: 0; font-size: 11px; color: var(--text-muted); } .markdown-cheatsheet code, .markdown-cheatsheet kbd { font-family: ui-monospace, Consolas, monospace; font-size: 11px; color: var(--text); overflow-wrap: anywhere; }
.is-fullscreen { position: fixed; inset: 0; z-index: 2200; border-radius: 0; display: flex; flex-direction: column; }
.is-fullscreen .editor-canvas, .is-fullscreen .editor-preview { flex: 1; min-height: 0; height: auto; } .is-fullscreen .editor-canvas > div { height: 100%; } .is-fullscreen .editor-canvas >>> .bytemd { height: 100%; }
.bytemd-view-container { min-width: 0; }
@media (max-width: 600px) { .editor-view-bar { padding: 0 8px; } .editor-view-tabs button { padding: 0 12px; } .editor-preview { padding: 20px 16px; } .editor-status { padding: 10px 12px; } .editor-image-hint { display: none; } .editor-canvas >>> .bytemd-toolbar { padding: 5px 8px; } .editor-canvas >>> .CodeMirror { font-size: 12px; } .markdown-cheatsheet { grid-template-columns: repeat(2, minmax(0, 1fr)); padding: 14px; } }
</style>
