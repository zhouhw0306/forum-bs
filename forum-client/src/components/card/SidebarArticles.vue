<template>
  <el-card class="community-card" shadow="never">
    <div slot="header" class="community-card-title"><i :class="recommended ? 'el-icon-reading' : 'el-icon-data-line'"></i><span>{{ recommended ? '值得一读' : '热门讨论' }}</span></div>
    <el-skeleton v-if="loading" :rows="3" animated />
    <div v-else-if="failed" class="sidebar-state">暂时无法加载 <button @click="load">重试</button></div>
    <ul v-else-if="articles.length" class="sidebar-articles"><li v-for="(article, index) in articles.slice(0, 5)" :key="article.id"><span v-if="!recommended" class="article-rank">{{ index + 1 }}</span><div><router-link :to="'/view/' + article.id">{{ article.title }}</router-link><span class="sidebar-date">{{ (article.createTime || '').slice(0, 10) }}</span></div></li></ul>
    <p v-else class="sidebar-state">{{ recommended ? '发现更多内容，从一次讨论开始。' : '精彩讨论即将出现在这里。' }}</p>
  </el-card>
</template>
<script>
import { getHot, pushArt } from '@/api'
export default {
  props: { recommended: Boolean },
  data: () => ({ articles: [], loading: true, failed: false }),
  mounted() { this.load() },
  methods: { async load() { this.loading = true; this.failed = false; try { const res = await (this.recommended ? pushArt() : getHot()); if (res.code !== 0) throw new Error(res.msg); this.articles = res.data || [] } catch (error) { this.failed = true } finally { this.loading = false } } }
}
</script>
<style scoped>
.sidebar-articles { list-style: none; padding: 0; margin: 0; }
.sidebar-articles li { display: flex; gap: 10px; padding: 10px 0; border-bottom: 1px solid var(--border-subtle); } .sidebar-articles li:first-child { padding-top: 0; } .sidebar-articles li:last-child { padding-bottom: 0; border-bottom: 0; }
.sidebar-articles li > div { min-width: 0; } .sidebar-articles a { color: var(--text); font-size: 13px; font-weight: 500; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.6; overflow-wrap: anywhere; } .sidebar-articles a:hover { color: var(--accent); }
.article-rank { color: #8c959f; min-width: 12px; padding-top: 2px; font-size: 12px; font-variant-numeric: tabular-nums; }
.sidebar-date { color: var(--text-muted); font-size: 11px; display: block; margin-top: 3px; }
</style>
