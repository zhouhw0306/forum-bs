<template>
  <div id="write">
    <the-header :simple="true"><span class="write-header-title"><i class="el-icon-edit-outline"></i> {{ articleForm.id ? '编辑讨论' : '新建讨论' }}</span><div class="write-header-actions"><span class="draft-state" :class="{ error: draftError }"><i :class="draftError ? 'el-icon-warning-outline' : (savingDraft ? 'el-icon-loading' : 'el-icon-check')"></i>{{ draftStatus }}</span><el-button size="small" :disabled="loadingArticle || articleLoadError" @click="saveDraft(true)">保存草稿</el-button><el-button type="primary" size="small" :disabled="loadingArticle || articleLoadError" :loading="publishing" @click="publishShow">发布讨论</el-button></div></the-header>
    <main class="write-page">
      <div class="write-page-heading"><div><div class="page-eyebrow"><router-link to="/">社区</router-link><i class="el-icon-arrow-right"></i><span>{{ articleForm.id ? '编辑讨论' : '发起讨论' }}</span></div><h1>分享你的下一次发现。</h1><p>一个问题、一段经验，都可以成为交流的开始。</p></div><button class="write-return" @click="cancel"><i class="el-icon-back"></i> 返回社区</button></div>
      <div v-if="articleLoadError" class="write-load-error state-panel" role="status"><i class="el-icon-warning-outline"></i><h3>讨论暂时无法加载</h3><p>本地草稿已保留，重新加载后可继续编辑。</p><el-button size="small" @click="initializeArticle(draftArticleId)">重新加载</el-button></div>
      <div v-else v-loading="loadingArticle" class="write-layout">
        <section class="write-content" aria-label="讨论内容">
          <div class="write-title-field"><label for="discussion-title">讨论标题 <span>用一句话，说清楚你想分享什么。</span></label><el-input id="discussion-title" v-model="articleForm.title" placeholder="给你的讨论起一个清晰的标题" maxlength="30" show-word-limit clearable /></div>
          <div class="write-body-label"><label>正文</label><span>支持代码高亮、表格与数学公式</span></div><markdown-editor :key="loadVersion" :editor="articleForm.editor" />
          <p class="write-bottom-note"><i class="el-icon-lock"></i> 草稿仅保存在当前浏览器，发布前其他人无法查看。</p>
        </section>
        <aside class="write-settings">
          <section class="write-settings-card"><h2><i class="el-icon-set-up"></i> 讨论设置</h2><el-form :model="articleForm" :rules="rules" ref="articleForm" label-position="top"><el-form-item label="所属分类" prop="category"><el-select v-model="articleForm.category" placeholder="选择最合适的分类" filterable><el-option v-for="category in categorys" :key="category.id" :label="category.categoryName" :value="category.id" /></el-select></el-form-item><el-form-item label="技术标签" prop="tags"><el-select v-model="articleForm.tags" multiple filterable placeholder="添加相关技术标签"><el-option v-for="tag in tags" :key="tag.id" :label="tag.tagName" :value="tag.id" /></el-select></el-form-item></el-form><p class="settings-hint">合适的分类和标签，可以让更多感兴趣的开发者发现你的讨论。</p><div v-if="optionsError" class="options-error">分类或标签加载失败 <button @click="getCategorysAndTags">重新加载</button></div></section>
          <section class="writing-tips"><span class="tips-icon"><i class="el-icon-reading"></i></span><h3>让交流更有收获</h3><ul><li>描述背景，以及你想解决的问题。</li><li>分享探索过程，而不只是结论。</li><li>用代码块和复现步骤补充细节。</li><li>保持友善，尊重不同的观点。</li></ul></section>
          <div class="local-draft-note"><i class="el-icon-document-checked"></i><div><strong>{{ restoredDraft ? '已恢复本地草稿' : '安心记录每一个想法' }}</strong><p>{{ draftError ? '本地保存失败，请复制内容后再离开。' : '编辑过程中会自动保存到当前浏览器。' }}</p></div></div>
        </aside>
      </div>
    </main>
    <el-dialog title="发布讨论" :visible.sync="publishVisible" width="480px" :close-on-click-modal="!publishing" :show-close="!publishing" :close-on-press-escape="!publishing" custom-class="publish-dialog"><div class="publish-summary"><span class="publish-summary-label">即将发布到社区</span><h3>{{ articleForm.title }}</h3><p><i class="el-icon-folder-opened"></i> {{ selectedCategory }}</p><div class="publish-summary-tags"><el-tag v-for="tag in selectedTags" :key="tag.id" size="small">{{ tag.tagName }}</el-tag></div><p class="publish-description">发布后，社区成员可以阅读并参与讨论。</p></div><span slot="footer"><el-button :disabled="publishing" @click="publishVisible = false">继续编辑</el-button><el-button type="primary" :loading="publishing" @click="publish">确认发布</el-button></span></el-dialog>
  </div>
</template>

<script>
import TheHeader from '@/components/TheHeader'
import MarkdownEditor from '@/components/article/MarkdownEditor'
import { publishArticle, getArticleById, getAllCategorys, getAllTags, getTags } from '@/api'
export default {
  name: 'BlogWrite', components: { TheHeader, MarkdownEditor },
  data() {
    return {
      publishVisible: false, publishing: false, categorys: [], tags: [], optionsError: false,
      ready: false, savingDraft: false, draftError: false, savedAt: null, restoredDraft: false,
      draftOwnerId: this.$store.getters.userId, draftArticleId: this.$route.params.id || 'new', loadVersion: 0, loadingArticle: false, articleLoadError: false,
      articleForm: { id: '', title: '', category: '', tags: [], editor: { value: '', ref: null, default_open: 'edit' } },
      rules: { category: [{ required: true, message: '请选择讨论分类', trigger: 'change' }], tags: [{ type: 'array', required: true, message: '请至少选择一个技术标签', trigger: 'change' }] }
    }
  },
  computed: {
    draftKey() { return 'codebase:discussion-draft:' + this.draftOwnerId + ':' + this.draftArticleId },
    draftSnapshot() { return { id: this.articleForm.id, title: this.articleForm.title, content: this.articleForm.editor.value, category: this.articleForm.category, tags: this.articleForm.tags } },
    draftStatus() { if (this.draftError) return '草稿保存失败'; if (this.savingDraft) return '正在保存…'; if (this.savedAt) return '草稿已保存 ' + new Date(this.savedAt).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }); return '尚未发布' },
    selectedCategory() { return this.categorys.find(category => category.id === this.articleForm.category)?.categoryName || '' },
    selectedTags() { return this.tags.filter(tag => this.articleForm.tags.includes(tag.id)) }
  },
  watch: { draftSnapshot: { deep: true, handler() { if (!this.ready) return; clearTimeout(this.draftTimer); this.savingDraft = true; this.draftTimer = setTimeout(() => this.saveDraft(false), 800) } } },
  mounted() {
    this.getCategorysAndTags()
    this.initializeArticle(this.$route.params.id)
    window.addEventListener('beforeunload', this.persistDraft)
  },
  beforeRouteUpdate(to, from, next) { if (this.ready) this.saveDraft(false); this.ready = false; clearTimeout(this.draftTimer); next(); this.initializeArticle(to.params.id) },
  beforeDestroy() { this.loadVersion += 1; clearTimeout(this.draftTimer); if (this.ready) this.saveDraft(false); window.removeEventListener('beforeunload', this.persistDraft) },
  methods: {
    async initializeArticle(id) {
      const version = ++this.loadVersion
      this.ready = false; this.loadingArticle = !!id; this.articleLoadError = false; this.publishVisible = false
      this.savingDraft = false; this.savedAt = null; this.restoredDraft = false; this.draftError = false; this.draftArticleId = id || 'new'
      this.articleForm = { id: id || '', title: '', category: '', tags: [], editor: { value: '', ref: null, default_open: 'edit' } }
      if (id && !(await this.loadArticle(id, version))) { if (version === this.loadVersion) this.loadingArticle = false; return }
      if (version !== this.loadVersion) return
      this.restoreDraft()
      await this.$nextTick()
      if (version === this.loadVersion) { this.ready = true; this.loadingArticle = false; this.$refs.articleForm?.clearValidate() }
    },
    persistDraft() { if (this.ready) this.saveDraft(false) },
    saveDraft(notify = false) {
      clearTimeout(this.draftTimer)
      if (!this.articleForm.title && !this.articleForm.editor.value) {
        try { localStorage.removeItem(this.draftKey); this.savedAt = null; this.restoredDraft = false; this.draftError = false; if (notify) this.$message.info('写下标题或正文后即可保存草稿') }
        catch (error) { this.draftError = true; if (notify) this.$message.error('本地草稿保存失败，请稍后重试') }
        this.savingDraft = false; return
      }
      try { const now = Date.now(); localStorage.setItem(this.draftKey, JSON.stringify({ ...this.draftSnapshot, savedAt: now })); this.savedAt = now; this.draftError = false; if (notify) this.$message.success('草稿已保存在当前浏览器') }
      catch (error) { this.draftError = true; if (notify) this.$message.error('本地草稿保存失败，请复制内容后再离开') }
      this.savingDraft = false
    },
    restoreDraft() {
      try {
        const draft = JSON.parse(localStorage.getItem(this.draftKey) || 'null')
        if (!draft || typeof draft.title !== 'string' || typeof draft.content !== 'string') return
        this.articleForm.title = draft.title; this.articleForm.editor.value = draft.content; this.articleForm.category = draft.category || ''; this.articleForm.tags = Array.isArray(draft.tags) ? draft.tags : []; this.savedAt = draft.savedAt; this.restoredDraft = true
      } catch (error) { this.draftError = true }
    },
    async loadArticle(id, version) {
      try {
        const params = new URLSearchParams(); params.append('articleId', id)
        const [response, tags] = await Promise.all([getArticleById(id), getTags(params)])
        if (version !== this.loadVersion) return false
        const article = response.data.data
        if (response.data.code !== 0 || !article || tags.code !== 0 || !Array.isArray(tags.data)) throw new Error('Load failed')
        this.articleForm.id = article.id; this.articleForm.title = article.title; this.articleForm.editor.value = article.content || ''; this.articleForm.category = article.categoryId; this.articleForm.tags = (tags.data || []).map(tag => tag.id)
        return true
      } catch (error) { if (version === this.loadVersion) this.articleLoadError = true; return false }
    },
    async getCategorysAndTags() {
      this.optionsError = false
      const results = await Promise.allSettled([getAllCategorys(), getAllTags()])
      results.forEach((result, index) => { if (result.status === 'fulfilled' && result.value.code === 0) { this[index ? 'tags' : 'categorys'] = result.value.data || [] } else this.optionsError = true })
    },
    publishShow() {
      if (!this.ready || this.loadingArticle || this.articleLoadError) return
      if (!this.articleForm.title.trim()) { this.$message.warning('请先填写讨论标题'); return }
      if (!this.articleForm.editor.value.trim()) { this.$message.warning('请先写下讨论正文'); return }
      this.$refs.articleForm.validate(valid => { if (valid) { this.saveDraft(false); this.publishVisible = true } })
    },
    async publish() {
      if (this.publishing || !this.ready || this.loadingArticle || this.articleLoadError) return
      this.publishing = true
      try {
        const response = await publishArticle({ id: this.articleForm.id, title: this.articleForm.title.trim(), categoryId: this.articleForm.category, tags: this.articleForm.tags.map(id => ({ id })), content: this.articleForm.editor.value, contentHtml: this.articleForm.editor.ref?.d_render || '' })
        if (response.data.code !== 0) throw new Error('Publish failed')
        this.ready = false; clearTimeout(this.draftTimer)
        try { localStorage.removeItem(this.draftKey) } catch (error) { /* Publishing succeeded even if browser storage is unavailable. */ }
        this.publishVisible = false; this.$message.success('讨论发布成功'); this.$router.replace('/view/' + response.data.data)
      } catch (error) { this.$message.error('发布失败，草稿已保留，请稍后重试') }
      finally { this.publishing = false }
    },
    cancel() { this.saveDraft(false); this.$router.push('/') }
  }
}
</script>

<style scoped>
#write { padding-top: 64px; min-height: 100vh; background: var(--canvas); }
.write-header-title { display: inline-flex; align-items: center; gap: 8px; font-size: 13px; font-weight: 600; color: var(--text-muted); white-space: nowrap; } .write-header-title i { font-size: 17px; }
.write-header-actions { display: flex; align-items: center; gap: 8px; margin-left: auto; } .write-header-actions .el-button { margin-left: 0; } .draft-state { display: flex; align-items: center; gap: 5px; margin-right: 8px; color: var(--text-muted); font-size: 11px; white-space: nowrap; } .draft-state > i { color: var(--success); } .draft-state.error, .draft-state.error > i { color: var(--danger); }
.write-page { max-width: 1360px; margin: auto; padding: 28px 32px 48px; }
.write-page-heading { display: flex; align-items: center; justify-content: space-between; gap: 24px; margin-bottom: 28px; } .write-page-heading h1 { font-size: 25px; line-height: 1.4; font-weight: 600; letter-spacing: -.6px; margin-bottom: 8px; } .write-page-heading p { color: var(--text-muted); font-size: 13px; }
.write-page-heading .page-eyebrow { font-size: 11px; } .page-eyebrow a { color: var(--text-muted); } .page-eyebrow i { font-size: 10px; }
.write-return { border: 0; background: transparent; color: var(--text-muted); font-size: 12px; cursor: pointer; white-space: nowrap; } .write-return:hover { color: var(--accent); }
.write-layout { display: grid; grid-template-columns: minmax(0, 1fr) 260px; align-items: start; gap: 24px; }
.write-content { min-width: 0; } .write-title-field { padding: 20px 24px; background: var(--surface); border: 1px solid var(--border); border-radius: 6px; }
.write-load-error { background: var(--surface); border: 1px solid var(--border); border-radius: 6px; }
.write-title-field > label { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; color: var(--text); font-size: 12px; font-weight: 600; } .write-title-field > label span { color: var(--text-muted); font-size: 11px; font-weight: 400; }
.write-title-field >>> .el-input__inner { height: 42px; font-size: 16px; padding-left: 0; padding-right: 68px; border-color: transparent; box-shadow: none; border-radius: 0; } .write-title-field >>> .el-input__inner:focus { border-color: transparent; box-shadow: none; } .write-title-field >>> .el-input__count-inner { color: var(--text-muted); font-size: 10px; }
.write-body-label { display: flex; justify-content: space-between; align-items: center; gap: 10px; margin: 22px 0 10px; } .write-body-label label { font-size: 12px; font-weight: 600; } .write-body-label span { font-size: 10px; color: var(--text-muted); }
.write-bottom-note { display: flex; align-items: center; gap: 5px; margin-top: 12px; color: var(--text-muted); font-size: 10px; }
.write-settings { position: sticky; top: 88px; min-width: 0; } .write-settings-card { border: 1px solid var(--border); border-radius: 6px; background: var(--surface); padding: 20px; } .write-settings-card h2 { display: flex; align-items: center; gap: 7px; font-size: 13px; font-weight: 600; margin-bottom: 20px; } .write-settings-card h2 i { color: var(--text-muted); font-size: 16px; }
.write-settings-card >>> .el-form-item { margin-bottom: 18px; } .write-settings-card >>> .el-form-item__label { font-size: 12px; padding-bottom: 7px; line-height: 1.5; } .write-settings-card >>> .el-select { width: 100%; } .write-settings-card >>> .el-input__inner { font-size: 12px; }
.settings-hint { color: var(--text-muted); font-size: 11px; line-height: 1.8; border-top: 1px solid var(--border-subtle); padding-top: 16px; }
.writing-tips { padding: 24px 20px; } .tips-icon { display: grid; place-items: center; width: 32px; height: 32px; border: 1px solid var(--border); border-radius: 7px; color: var(--text-muted); font-size: 18px; background: var(--surface); margin-bottom: 12px; } .writing-tips h3 { font-size: 12px; font-weight: 600; margin-bottom: 12px; } .writing-tips ul { padding-left: 16px; color: var(--text-muted); font-size: 11px; line-height: 2.1; }
.local-draft-note { display: flex; gap: 10px; padding: 16px 20px; border-top: 1px solid var(--border); color: var(--text-muted); } .local-draft-note > i { font-size: 18px; margin-top: 2px; } .local-draft-note strong { color: var(--text); font-size: 11px; font-weight: 500; } .local-draft-note p { font-size: 11px; line-height: 1.8; margin-top: 5px; }
.options-error { color: var(--danger); font-size: 11px; margin-top: 12px; } .options-error button { background: transparent; color: var(--accent); border: 0; cursor: pointer; font-size: 11px; }
.publish-summary-label { color: var(--text-muted); font-size: 12px; } .publish-summary h3 { font-size: 20px; font-weight: 600; line-height: 1.5; margin: 12px 0 16px; overflow-wrap: anywhere; } .publish-summary p { color: var(--text-muted); font-size: 12px; } .publish-summary-tags { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 12px; } .publish-description { border-top: 1px solid var(--border-subtle); padding-top: 16px; margin-top: 24px; }
@media (max-width: 1100px) { .draft-state { display: none; } .write-layout { grid-template-columns: minmax(0, 1fr) 240px; gap: 20px; } }
@media (max-width: 900px) { .write-header-title { display: none; } .write-page { padding: 24px; } .write-layout { grid-template-columns: minmax(0, 1fr); } .write-settings { position: static; display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; } .local-draft-note { grid-column: 1 / -1; } .writing-tips { padding: 20px; } }
@media (max-width: 600px) { .write-page { padding: 24px 16px 32px; } .write-page-heading { align-items: flex-start; gap: 12px; } .write-page-heading h1 { font-size: 22px; } .write-page-heading p { font-size: 12px; } .write-return { display: none; } .write-title-field { padding: 16px; } .write-title-field > label { flex-wrap: wrap; gap: 5px; } .write-title-field > label span { font-size: 10px; } .write-title-field >>> .el-input__inner { font-size: 14px; } .write-settings { display: block; } .writing-tips { padding: 24px 16px; } .local-draft-note { padding: 16px; } .write-header-actions { gap: 6px; } .write-header-actions .el-button { padding: 9px 10px; font-size: 11px; } }
</style>
