<template>
  <div class="ai-chat">
    <!-- 消息区 -->
    <div class="ai-msg-area" ref="msgBox">
      <div class="ai-welcome" v-if="messages.length === 0">
        <img src="https://deepseek.com/favicon.ico" class="ai-welcome-icon" />
        <h3>DeepSeek AI</h3>
        <p>有什么可以帮你的？</p>
      </div>
      <div v-for="(msg, i) in messages" :key="i" :class="['ai-msg', msg.role]">
        <img class="ai-msg-avatar"
          :src="msg.role === 'user' ? avatar : 'https://deepseek.com/favicon.ico'" />
        <div class="ai-msg-bubble" v-html="msg.html"></div>
      </div>
    </div>

    <!-- 输入区 -->
    <div class="ai-input-area">
      <div class="ai-input-row">
        <textarea
          ref="inputEl"
          v-model="input"
          class="ai-textarea"
          placeholder="输入消息，Enter 发送，Shift+Enter 换行"
          rows="1"
          @keydown.enter.exact.prevent="send"
          @input="autoResize"
          :disabled="loading"
        ></textarea>
        <button
          class="ai-send-btn"
          :class="{ loading: loading }"
          :disabled="!input.trim() || loading"
          @click="send"
        >
          <i v-if="!loading" class="el-icon-position"></i>
          <i v-else class="el-icon-loading"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { marked } from "marked";

const API_BASE = "http://localhost:8888";

export default {
  name: "AiModel",
  data() {
    return {
      input: "",
      messages: [],
      loading: false,
    };
  },
  computed: {
    ...mapGetters(["avatar", "username"]),
  },
  methods: {
    autoResize() {
      const el = this.$refs.inputEl;
      if (!el) return;
      el.style.height = "auto";
      el.style.height = Math.min(el.scrollHeight, 120) + "px";
    },
    async send() {
      const issue = this.input.trim();
      if (!issue || this.loading) return;
      this.input = "";
      this.loading = true;

      // 重置 textarea 高度
      this.$nextTick(() => {
        const el = this.$refs.inputEl;
        if (el) el.style.height = "auto";
      });

      // 用户消息
      this.messages.push({ role: "user", text: issue, html: this.escape(issue) });
      this.scrollBottom();

      // AI 空消息占位（带光标）
      const aiIdx = this.messages.length;
      this.messages.push({ role: "assistant", text: "", html: '<span class="ai-cursor">|</span>' });
      this.scrollBottom();

      try {
        const url = `${API_BASE}/chat/ask?issue=${encodeURIComponent(issue)}`;
        const resp = await fetch(url);
        const reader = resp.body.getReader();
        const decoder = new TextDecoder("utf-8");
        let buf = "";

        let lastRender = 0;
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;
          buf += decoder.decode(value, { stream: true });
          const lines = buf.split("\n");
          buf = lines.pop() || "";
          for (const line of lines) {
            if (line.startsWith("data:")) {
              this.messages[aiIdx].text += line.slice(5).replace(/^ /, "");
            }
          }
          // 节流 80ms 更新 DOM，避免频繁替换打断文本选中
          const now = Date.now();
          if (now - lastRender > 80) {
            this.messages[aiIdx].html = this.text2html(this.messages[aiIdx].text);
            this.scrollBottom();
            lastRender = now;
          }
        }
        this.messages[aiIdx].html = this.md2html(this.sanitizeText(this.messages[aiIdx].text));
        this.scrollBottom();
      } catch (e) {
        this.messages[aiIdx].text += "\n\n*连接超时或失败*";
        this.messages[aiIdx].html = this.md2html(this.sanitizeText(this.messages[aiIdx].text));
      } finally {
        this.loading = false;
        this.scrollBottom();
      }
    },
    // 闭合未完成的代码块，防止截断时 HTML 注入
    sanitizeText(text) {
      const ticks = (text.match(/```/g) || []).length;
      return ticks % 2 !== 0 ? text + "\n```" : text;
    },
    text2html(text) {
      return this.escape(text).replace(/\n/g, "<br>");
    },
    md2html(text) {
      try {
        // 手动安全处理：先提取代码块再渲染普通 markdown，防止 HTML 注入
        const codeBlocks = [];
        const safeText = text.replace(/```(\w*)\n?([\s\S]*?)```/g, (_, lang, code) => {
          codeBlocks.push({ lang, code: this.escape(code.trimEnd()) });
          return `%%CODEBLOCK_${codeBlocks.length - 1}%%`;
        });
        marked.setOptions({ breaks: true });
        let html = marked(safeText);
        // 还原代码块，确保内容已转义
        codeBlocks.forEach((block, i) => {
          const langClass = block.lang ? ` class="language-${block.lang}"` : "";
          html = html.replace(
            `%%CODEBLOCK_${i}%%`,
            `<pre><code${langClass}>${block.code}</code></pre>`
          );
        });
        return html;
      } catch (e) {
        return this.escape(text).replace(/\n/g, "<br>");
      }
    },
    escape(s) {
      return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    },
    scrollBottom() {
      this.$nextTick(() => {
        const el = this.$refs.msgBox;
        if (el) el.scrollTop = el.scrollHeight;
      });
    },
  },
};
</script>

<style scoped>
.ai-chat {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  background: #f0f2f5;
}

/* ---- 消息区 ---- */
.ai-msg-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
}
.ai-welcome {
  text-align: center;
  margin-top: 80px;
}
.ai-welcome-icon {
  width: 56px; height: 56px;
  border-radius: 14px;
  margin-bottom: 12px;
}
.ai-welcome h3 { margin: 0 0 4px; font-size: 20px; color: #1a1a2e; }
.ai-welcome p  { margin: 0; font-size: 14px; color: #909399; }

.ai-msg {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
  max-width: 88%;
}
.ai-msg.user {
  flex-direction: row-reverse;
  margin-left: auto;
}
.ai-msg-avatar {
  width: 34px; height: 34px;
  border-radius: 8px;
  flex-shrink: 0;
  object-fit: cover;
}
.ai-msg-bubble {
  background: #fff;
  border-radius: 4px 14px 14px 14px;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.7;
  color: #303133;
  box-shadow: 0 1px 3px rgba(0,0,0,.05);
  word-break: break-word;
  min-width: 24px;
  user-select: text;
}
.user .ai-msg-bubble {
  background: #409eff;
  color: #fff;
  border-radius: 14px 4px 14px 14px;
}
.ai-cursor {
  animation: blink 0.7s infinite;
  color: #409eff; font-weight: 700;
}
@keyframes blink {
  0%,100% { opacity: 1; } 50% { opacity: 0; }
}

/* ---- 输入区（悬浮） ---- */
.ai-input-area {
  position: sticky;
  bottom: 16px;
  margin: 0 16px 16px;
  padding: 0;
  background: transparent;
}
.ai-input-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  background: #fff;
  border-radius: 16px;
  padding: 8px 8px 8px 18px;
  box-shadow: 0 4px 20px rgba(0,0,0,.08), 0 0 0 1px rgba(0,0,0,.04);
  transition: box-shadow .25s;
}
.ai-input-row:focus-within {
  box-shadow: 0 4px 24px rgba(64,158,255,.15), 0 0 0 2px rgba(64,158,255,.2);
}
.ai-textarea {
  flex: 1;
  border: none;
  outline: none;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  background: transparent;
  color: #303133;
  font-family: inherit;
  padding: 4px 0;
  min-height: 24px;
  max-height: 120px;
}
.ai-textarea::placeholder { color: #b0b5be; }
.ai-textarea:disabled { opacity: .5; }
.ai-send-btn {
  width: 38px; height: 38px;
  border-radius: 10px;
  border: none;
  background: #409eff;
  color: #fff;
  font-size: 18px;
  cursor: pointer;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background .2s, transform .1s, opacity .2s;
}
.ai-send-btn:hover:not(:disabled) { background: #337ecc; transform: scale(1.05); }
.ai-send-btn:disabled { opacity: .35; cursor: not-allowed; }
.ai-send-btn.loading { background: #909399; }
</style>

<!-- markdown 代码块 + highlight.js 主题（全局，v-html 需要） -->
<style>
/* --- 代码块容器 --- */
.ai-msg-bubble pre {
  display: block;
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 12px 14px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 8px 0;
  font-size: 13px;
  line-height: 1.55;
}
.ai-msg-bubble pre code {
  padding: 0;
  background: none !important;
  color: #d4d4d4;
  font-size: 13px;
  font-family: "Fira Code","Consolas",monospace;
}

/* --- 内联代码 --- */
.ai-msg-bubble p code {
  padding: 2px 6px;
  background: #f0f0f0;
  color: #e74c3c;
  border-radius: 3px;
  font-size: 13px;
  font-family: "Fira Code","Consolas",monospace;
}


/* --- 段落/列表/表格 --- */
.ai-msg-bubble p { margin: 4px 0; }
.ai-msg-bubble ul, .ai-msg-bubble ol { padding-left: 18px; margin: 4px 0; }
.ai-msg-bubble table { border-collapse: collapse; margin: 8px 0; width: 100%; }
.ai-msg-bubble th, .ai-msg-bubble td { border: 1px solid #ddd; padding: 6px 10px; text-align: left; }
.ai-msg-bubble th { background: #f5f5f5; }

/* --- 用户气泡反色 --- */
.ai-msg.user .ai-msg-bubble pre {
  background: rgba(0,0,0,.2);
  color: rgba(255,255,255,.9);
}
.ai-msg.user .ai-msg-bubble pre code { color: rgba(255,255,255,.9); }
.ai-msg.user .ai-msg-bubble p code {
  background: rgba(255,255,255,.2);
  color: rgba(255,255,255,.9);
}
</style>
