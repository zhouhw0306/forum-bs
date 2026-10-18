<template>
  <section class="ai-assistant" aria-label="AI 编程助手">
    <header class="assistant-header">
      <div class="assistant-identity"><span class="assistant-logo"><i class="el-icon-magic-stick"></i></span><div><h2>AI 编程助手</h2><p>和你的下一次灵感，一起写代码。</p></div></div>
      <div class="assistant-header-actions"><button class="assistant-icon-button" :disabled="loading || !messages.length" @click="newConversation" aria-label="开始新对话" title="新对话"><i class="el-icon-edit-outline"></i></button><button class="assistant-icon-button" @click="close" aria-label="关闭 AI 助手"><i class="el-icon-close"></i></button></div>
    </header>
    <div class="assistant-model-bar"><span><span class="model-dot"></span> DeepSeek</span><span>{{ loading ? '正在生成回答…' : '编程 · 学习 · 灵感' }}</span></div>
    <div class="assistant-messages" ref="msgBox" @scroll="onScroll">
      <div v-if="!messages.length" class="assistant-welcome">
        <span class="welcome-symbol"><i class="el-icon-magic-stick"></i></span><span class="welcome-eyebrow">从一个问题开始</span><h3>让想法，更进一步。</h3><p>解释代码、排查问题，或一起梳理实现思路。<br />把你正在思考的问题写下来。</p>
        <div class="assistant-suggestions"><button v-for="suggestion in suggestions" :key="suggestion.title" @click="useSuggestion(suggestion.prompt)"><i :class="suggestion.icon"></i><strong>{{ suggestion.title }}</strong><span>{{ suggestion.description }}</span><i class="el-icon-top-right suggestion-arrow"></i></button></div>
      </div>
      <article v-for="(message, index) in messages" :key="message.id" :class="['assistant-message', message.role]">
        <div class="message-heading"><span v-if="message.role === 'assistant'" class="message-ai-symbol"><i class="el-icon-magic-stick"></i></span><img v-else :src="attachImageUrl(avatar)" :alt="username || '我'" @error="e => e.target.src = avatarFallback(username || 'U')" /><strong>{{ message.role === 'assistant' ? 'CodeBase AI' : (username || '我') }}</strong><span v-if="message.role === 'assistant'" class="message-model">DeepSeek</span></div>
        <div class="assistant-message-body"><p v-if="message.role === 'user'" class="user-message-text">{{ message.text }}</p><div v-else-if="message.status === 'generating' && !message.displayText" class="assistant-thinking" role="status"><span></span><span></span><span></span><span>正在思考</span></div><Viewer v-else-if="message.displayText" :value="closedMarkdown(message.displayText)" :plugins="plugins" />
          <div v-if="message.status === 'error'" class="message-error" role="status"><i class="el-icon-warning-outline"></i><span>{{ message.error }}</span><button :disabled="loading" @click="retry(index)">重试</button></div>
          <div v-if="message.role === 'assistant' && message.status !== 'generating' && message.text" class="message-actions"><button @click="copy(message)" :aria-label="'复制第 ' + (index + 1) + ' 条消息'"><i :class="copiedId === message.id ? 'el-icon-check' : 'el-icon-document-copy'"></i> {{ copiedId === message.id ? '已复制' : '复制回答' }}</button><span v-if="message.status === 'stopped'">已停止生成</span></div>
        </div>
      </article>
    </div>
    <footer class="assistant-composer">
      <div class="composer-box"><textarea ref="inputEl" v-model="input" rows="2" maxlength="8000" aria-label="向 AI 提问" placeholder="描述你的问题，或粘贴一段代码…" @keydown.enter.exact="handleEnter" @input="autoResize"></textarea><div class="composer-toolbar"><span><i class="el-icon-edit-outline"></i> {{ input.length ? input.length + ' / 8000' : '支持 Markdown 与代码块' }}</span><button v-if="loading" class="stop-generation" @click="stop"><i class="el-icon-video-pause"></i> 停止生成</button><button v-else class="send-message" :disabled="!input.trim()" @click="send" aria-label="发送消息">发送 <i class="el-icon-top"></i></button></div></div>
      <p class="composer-hint"><span>Enter 发送 · Shift + Enter 换行</span><span>AI 回答仅供参考</span></p>
    </footer>
  </section>
</template>

<script>
import { mapGetters } from 'vuex'
import { Viewer } from '@bytemd/vue'
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import { mixin } from '@/mixins'
import { createSseTextParser } from '@/utils/sse'
import 'bytemd/dist/index.css'
const API_BASE = 'http://localhost:8888'
export default {
  name: 'AiModel', components: { Viewer }, mixins: [mixin],
  props: { active: { type: Boolean, default: true } },
  data() {
    return {
      input: '', messages: [], loading: false, controller: null, requestVersion: 0, followOutput: true, copiedId: null,
      plugins: [gfm(), highlight()],
      suggestions: [
        { title: '读懂一段代码', description: '解释原理与执行过程', icon: 'el-icon-document', prompt: '请帮我解释下面这段代码的作用和执行过程：\n\n' },
        { title: '一起排查错误', description: '定位原因，找到解决方法', icon: 'el-icon-first-aid-kit', prompt: '我遇到了下面的问题，请帮我分析可能的原因和排查步骤：\n\n' },
        { title: '改进代码质量', description: '让实现更清晰、更高效', icon: 'el-icon-set-up', prompt: '请从可读性、性能和边界情况三个方面，帮我改进下面的代码：\n\n' },
        { title: '梳理实现思路', description: '从需求走向具体方案', icon: 'el-icon-connection', prompt: '请帮我把下面的需求拆解为可执行的开发步骤：\n\n' }
      ]
    }
  },
  computed: { ...mapGetters(['avatar', 'username']) },
  watch: { active(value) { if (!value) this.stop() } },
  beforeDestroy() { this.stop(); this.requestVersion += 1; clearTimeout(this.copyTimer) },
  methods: {
    handleEnter(event) { if (event.isComposing || event.keyCode === 229) return; event.preventDefault(); this.send() },
    autoResize() { const el = this.$refs.inputEl; if (el) { el.style.height = 'auto'; el.style.height = Math.min(el.scrollHeight, 180) + 'px' } },
    useSuggestion(prompt) { this.input = prompt; this.$nextTick(() => { this.autoResize(); this.$refs.inputEl?.focus() }) },
    newConversation() { if (this.loading) return; this.messages = []; this.input = ''; this.$nextTick(this.autoResize) },
    close() { this.stop(); this.$emit('close') },
    stop() { if (this.controller) this.controller.abort() },
    closedMarkdown(text) { return ((text.match(/\x60{3}/g) || []).length % 2) ? text + '\n' + String.fromCharCode(96).repeat(3) : text },
    onScroll() { const el = this.$refs.msgBox; if (el) this.followOutput = el.scrollHeight - el.scrollTop - el.clientHeight < 80 },
    scrollBottom(force = false) { this.$nextTick(() => { const el = this.$refs.msgBox; if (el && (force || this.followOutput)) el.scrollTop = el.scrollHeight }) },
    async copy(message) { try { await navigator.clipboard.writeText(message.text); this.copiedId = message.id; clearTimeout(this.copyTimer); this.copyTimer = setTimeout(() => { this.copiedId = null }, 1800) } catch (error) { this.$message.info('请选中回答内容后复制') } },
    async send() {
      const issue = this.input.trim()
      if (!issue || this.loading) return
      this.input = ''; this.followOutput = true; this.$nextTick(this.autoResize)
      this.messages.push({ id: Date.now() + '-user', role: 'user', text: issue })
      const message = { id: Date.now() + '-assistant', role: 'assistant', text: '', displayText: '', status: 'generating', error: '' }
      this.messages.push(message); this.scrollBottom(true)
      await this.streamAnswer(issue, message)
    },
    async retry(index) { if (this.loading) return; const question = this.messages[index - 1]; if (question?.role === 'user') await this.streamAnswer(question.text, this.messages[index]) },
    async streamAnswer(issue, message) {
      const version = ++this.requestVersion
      const controller = new AbortController(); this.controller = controller; this.loading = true
      message.text = ''; message.displayText = ''; message.status = 'generating'; message.error = ''
      let reader
      try {
        const response = await fetch(API_BASE + '/chat/ask?issue=' + encodeURIComponent(issue), { signal: controller.signal })
        if (!response.ok || !response.body) throw new Error('AI 服务暂时无法连接，请稍后重试。')
        reader = response.body.getReader()
        const decoder = new TextDecoder('utf-8')
        let lastRender = 0
        const parser = createSseTextParser(text => { message.text += text })
        while (true) {
          const chunk = await reader.read()
          if (chunk.done) break
          parser.push(decoder.decode(chunk.value, { stream: true }))
          if (Date.now() - lastRender > 100) { message.displayText = message.text; this.scrollBottom(); lastRender = Date.now() }
        }
        parser.push(decoder.decode()); parser.finish()
        if (!message.text.trim() || ['*服务暂时不可用*', 'AI 服务响应异常'].includes(message.text.trim())) { message.text = ''; throw new Error('AI 服务暂时不可用，请检查服务配置后重试。') }
        if (message.text.endsWith('*响应超时*')) { message.text = message.text.slice(0, -6).trimEnd(); throw new Error('回答超时，已保留生成的内容。') }
        message.status = 'complete'
      } catch (error) {
        if (version !== this.requestVersion) return
        if (error.name === 'AbortError') message.status = 'stopped'
        else { message.status = 'error'; message.error = error instanceof TypeError ? '连接失败，请检查网络后重试。' : (error.message || '连接失败，请稍后重试。') }
      } finally {
        if (reader) reader.releaseLock()
        if (version === this.requestVersion) { message.displayText = message.text; this.loading = false; this.controller = null; this.scrollBottom() }
      }
    }
  }
}
</script>

<style scoped>
.ai-assistant { display: flex; flex-direction: column; height: 100vh; height: 100dvh; min-width: 0; background: var(--surface); color: var(--text); }
.assistant-header { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 20px 24px; border-bottom: 1px solid var(--border-subtle); }
.assistant-identity { display: flex; align-items: center; gap: 12px; min-width: 0; } .assistant-logo { display: grid; place-items: center; width: 36px; height: 36px; border-radius: 9px; background: var(--text); color: white; font-size: 21px; flex-shrink: 0; }
.assistant-identity h2 { font-size: 15px; font-weight: 600; margin: 0; } .assistant-identity p { margin: 3px 0 0; font-size: 11px; color: var(--text-muted); }
.assistant-header-actions { display: flex; align-items: center; gap: 6px; } .assistant-icon-button { display: grid; place-items: center; width: 30px; height: 30px; border: 0; border-radius: 6px; color: var(--text-muted); background: transparent; font-size: 18px; cursor: pointer; } .assistant-icon-button:hover:not(:disabled) { background: var(--hover); color: var(--text); } .assistant-icon-button:disabled { opacity: .35; cursor: default; }
.assistant-model-bar { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 10px 24px; border-bottom: 1px solid var(--border-subtle); background: var(--surface-muted); color: var(--text-muted); font-size: 11px; }
.assistant-model-bar > span:first-child { display: flex; align-items: center; gap: 7px; color: var(--text); font-weight: 500; } .model-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--success); }
.assistant-messages { flex: 1; min-height: 0; overflow-y: auto; overscroll-behavior: contain; padding: 28px 28px 16px; scroll-padding-bottom: 20px; }
.assistant-welcome { max-width: 490px; margin: 8px auto 16px; } .welcome-symbol { display: grid; place-items: center; width: 40px; height: 40px; background: var(--surface-muted); border: 1px solid var(--border-subtle); border-radius: 10px; font-size: 23px; margin-bottom: 20px; color: var(--text); }
.welcome-eyebrow { font-size: 11px; color: var(--text-muted); } .assistant-welcome h3 { font-size: 28px; font-weight: 600; letter-spacing: -1px; margin: 10px 0 12px; } .assistant-welcome > p { color: var(--text-muted); font-size: 13px; line-height: 1.8; }
.assistant-suggestions { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; margin-top: 24px; }
.assistant-suggestions button { position: relative; display: grid; grid-template-columns: 20px minmax(0, 1fr); gap: 6px 10px; padding: 16px; text-align: left; background: var(--surface); border: 1px solid var(--border); border-radius: 8px; cursor: pointer; color: var(--text); }
.assistant-suggestions button > i:first-child { grid-row: 1 / 3; }
.assistant-suggestions button > strong, .assistant-suggestions button > span { grid-column: 2; }
.assistant-suggestions button:hover { background: var(--surface-muted); border-color: #afb8c1; } .assistant-suggestions button > i:first-child { font-size: 19px; color: var(--text-muted); margin-bottom: 4px; } .assistant-suggestions strong { font-size: 12px; font-weight: 600; } .assistant-suggestions button > span { color: var(--text-muted); font-size: 11px; } .assistant-suggestions .suggestion-arrow { position: absolute; top: 16px; right: 14px; color: #8c959f; font-size: 14px; }
.assistant-message { margin-bottom: 28px; min-width: 0; } .message-heading { display: flex; align-items: center; gap: 8px; font-size: 12px; margin-bottom: 12px; } .message-heading img, .message-ai-symbol { width: 25px; height: 25px; border-radius: 6px; border: 1px solid var(--border-subtle); object-fit: cover; } .message-ai-symbol { display: grid; place-items: center; background: var(--surface-muted); font-size: 15px; } .message-heading strong { font-weight: 600; } .message-model { color: var(--text-muted); font-size: 10px; font-weight: 400; }
.assistant-message-body { padding-left: 33px; min-width: 0; } .user-message-text { margin: 0; padding: 12px 14px; border: 1px solid var(--border-subtle); border-radius: 6px; background: var(--surface-muted); font-size: 13px; line-height: 1.7; white-space: pre-wrap; overflow-wrap: anywhere; }
.assistant-message-body >>> .markdown-body { font-size: 13px; } .assistant-message-body >>> .markdown-body h1 { font-size: 1.4em; } .assistant-message-body >>> .markdown-body h2 { font-size: 1.2em; } .assistant-message-body >>> .markdown-body pre code { font-size: 12px; }
.assistant-thinking { display: flex; gap: 4px; align-items: center; color: var(--text-muted); font-size: 12px; padding-top: 6px; } .assistant-thinking span:not(:last-child) { width: 4px; height: 4px; border-radius: 50%; background: #8c959f; animation: pulse 1.2s ease-in-out infinite; } .assistant-thinking span:nth-child(2) { animation-delay: .15s; } .assistant-thinking span:nth-child(3) { animation-delay: .3s; } .assistant-thinking span:last-child { margin-left: 5px; }
.message-actions { display: flex; align-items: center; gap: 12px; font-size: 10px; color: var(--text-muted); margin-top: 12px; } .message-actions button, .message-error button { background: none; border: 0; padding: 0; color: var(--text-muted); font-size: 11px; cursor: pointer; } .message-actions button:hover, .message-error button { color: var(--accent); }
.message-error { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; padding: 12px; border: 1px solid #d4a72c66; border-radius: 6px; background: #fff8c5; font-size: 12px; color: #7d4e00; }
.assistant-composer { flex-shrink: 0; padding: 12px 24px 16px; background: var(--surface); border-top: 1px solid var(--border-subtle); }
.composer-box { border: 1px solid var(--border); border-radius: 8px; background: var(--surface); padding: 12px; } .composer-box:focus-within { border-color: var(--accent); outline: 2px solid var(--focus); }
.composer-box textarea { display: block; width: 100%; border: 0; outline: none; resize: none; min-height: 44px; max-height: 180px; background: transparent; color: var(--text); font-size: 13px; line-height: 1.7; }
.composer-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 8px; margin-top: 10px; } .composer-toolbar > span { color: var(--text-muted); font-size: 10px; } .composer-toolbar > span i { margin-right: 3px; }
.send-message, .stop-generation { display: inline-flex; align-items: center; justify-content: center; gap: 7px; padding: 6px 12px; border: 1px solid #1b1f2426; border-radius: 6px; background: var(--success); color: #fff; font-size: 12px; font-weight: 500; cursor: pointer; } .send-message:disabled { opacity: .45; cursor: default; } .send-message:hover:not(:disabled) { background: #1a7f37; } .stop-generation { border-color: var(--border); background: var(--surface-muted); color: var(--text); }
.composer-hint { display: flex; align-items: center; justify-content: space-between; gap: 8px; margin: 10px 0 0; font-size: 10px; color: var(--text-muted); }
@keyframes pulse { 0%, 100% { opacity: .3; } 50% { opacity: 1; } }
@media (max-width: 600px) { .assistant-header { padding: 16px; } .assistant-identity p { display: none; } .assistant-model-bar { padding: 10px 16px; } .assistant-messages { padding: 24px 16px 12px; } .assistant-welcome { margin-top: 8px; } .assistant-welcome h3 { font-size: 25px; } .assistant-suggestions { gap: 8px; margin-top: 22px; } .assistant-suggestions button { padding: 12px; } .assistant-suggestions .suggestion-arrow { top: 12px; right: 10px; } .assistant-suggestions button > span { font-size: 10px; } .assistant-composer { padding: 12px 16px max(12px, env(safe-area-inset-bottom)); } .assistant-message-body { padding-left: 0; } .composer-hint { font-size: 9px; } }
</style>
