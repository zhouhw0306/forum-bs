<template>
  <article class="discussion-item">
    <div class="discussion-icon"><i class="el-icon-chat-dot-round"></i></div>
    <div class="discussion-content">
      <div class="discussion-title-row"><router-link :to="'/view/' + id" class="discussion-title">{{ title }}</router-link></div>
      <p class="discussion-excerpt">{{ excerpt }}</p>
      <div class="discussion-tags" v-if="tags.length"><el-tag v-for="tag in tags" :key="tag.id || tag.tagName" size="mini">{{ tag.tagName }}</el-tag></div>
      <div class="discussion-meta"><span class="discussion-author">{{ author.username || '社区成员' }}</span><span>发布于 {{ createTime ? createTime.slice(0, 10) : '最近' }}</span><span class="discussion-views"><i class="el-icon-view"></i> {{ viewCount || 0 }}</span></div>
    </div>
    <router-link :to="'/view/' + id" class="discussion-replies" :aria-label="(commentCount || 0) + ' 条评论'"><i class="el-icon-chat-line-round"></i><span>{{ commentCount || 0 }}</span></router-link>
  </article>
</template>
<script>
import { getAuthorById, getTags } from '@/api'
import { toExcerpt } from '@/utils/content'
export default {
  name: 'ArticleItem',
  props: { id: String, categoryId: Number, title: String, commentCount: Number, viewCount: Number, content: String, contentHtml: String, createTime: String, userId: String, updateTime: String },
  data: () => ({ author: {}, tags: [] }),
  computed: { excerpt() { return toExcerpt(this.content || this.contentHtml) } },
  mounted() { this.loadAuthor(); this.loadTags() },
  methods: {
    async loadAuthor() {
      if (!this.userId) return
      try { const result = await getAuthorById(this.userId); if (result.code === 0) this.author = result.data || {} } catch (error) { this.author = {} }
    },
    async loadTags() {
      const params = new URLSearchParams(); params.append('articleId', this.id)
      try { const result = await getTags(params); if (result.code === 0) this.tags = result.data || [] } catch (error) { this.tags = [] }
    }
  }
}
</script>
<style scoped>
.discussion-item { display: flex; gap: 12px; padding: 22px 20px; border-bottom: 1px solid var(--border-subtle); background: var(--surface); }
.discussion-item:last-child { border-bottom: 0; } .discussion-item:hover { background: #fcfcfd; }
.discussion-icon { flex-shrink: 0; color: var(--success); font-size: 19px; padding-top: 1px; }
.discussion-content { flex: 1; min-width: 0; }
.discussion-title { display: block; color: var(--text); font-size: 16px; line-height: 1.5; font-weight: 600; overflow-wrap: anywhere; }
.discussion-title:hover { color: var(--accent); }
.discussion-excerpt { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin: 7px 0 11px; font-size: 13px; line-height: 1.7; color: var(--text-muted); overflow-wrap: anywhere; }
.discussion-tags { display: flex; flex-wrap: wrap; gap: 5px; margin-bottom: 9px; }
.discussion-meta { display: flex; align-items: center; flex-wrap: wrap; gap: 5px 10px; font-size: 12px; color: var(--text-muted); }
.discussion-author { font-weight: 500; color: #57606a; } .discussion-views { display: flex; align-items: center; gap: 4px; }
.discussion-replies { display: flex; align-items: center; gap: 5px; color: var(--text-muted); font-size: 12px; align-self: flex-start; padding-top: 4px; min-width: 28px; } .discussion-replies i { font-size: 17px; }
@media (max-width: 600px) { .discussion-item { padding: 18px 16px; gap: 9px; } .discussion-title { font-size: 15px; } .discussion-meta { font-size: 11px; gap: 5px 8px; } .discussion-views { display: none; } }
</style>
