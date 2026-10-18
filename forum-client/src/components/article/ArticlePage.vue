<template>
  <section class="discussion-feed" aria-label="讨论列表">
    <div class="feed-toolbar"><span><i class="el-icon-chat-line-square"></i> {{ isCare ? '关注的讨论' : (typeIndex ? '分类讨论' : '最新讨论') }}</span><span class="feed-sort"><i class="el-icon-sort"></i> 按发布时间</span></div>
    <div v-if="needsLogin" class="state-panel"><i class="el-icon-star-off"></i><h3>发现你关心的讨论</h3><p>登录后，查看你关注的开发者分享的内容。</p><router-link :to="{ path: '/login', query: { redirect: $route.fullPath } }" class="button-primary">登录社区</router-link></div>
    <scroll-page v-else :loading="loading" :offset="offset" :no-data="noData || !!error" @load="load">
      <article-item v-for="article in articles" :key="article.id" v-bind="article" />
      <div v-if="loading && !articles.length" class="feed-skeleton"><el-skeleton v-for="index in 3" :key="index" :rows="3" animated /></div>
      <div v-if="error" class="state-panel" role="status"><i class="el-icon-warning-outline"></i><h3>讨论暂时无法加载</h3><p>请检查网络连接后重试。</p><el-button size="small" @click="getArticles">重新加载</el-button></div>
      <div v-else-if="!loading && !articles.length" class="state-panel"><i class="el-icon-chat-dot-round"></i><h3>{{ isCare ? '还没有关注动态' : '这里还没有讨论' }}</h3><p>{{ isCare ? '关注感兴趣的开发者，他们的新讨论会出现在这里。' : '分享一个想法，开启这里的第一场交流。' }}</p><router-link to="/write">发起讨论 <i class="el-icon-right"></i></router-link></div>
      <div v-if="noData && articles.length" class="feed-end">你已经看完了全部讨论</div>
    </scroll-page>
  </section>
</template>
<script>
import ArticleItem from '@/components/article/ArticleItem'
import ScrollPage from '@/components/ScrollPage'
import { getArticles } from '@/api'
export default {
  name: 'ArticleScrollPage',
  components: { ArticleItem, ScrollPage },
  props: { offset: { type: Number, default: 100 }, isCareMe: { type: Boolean, default: false } },
  data: () => ({ loading: false, noData: false, error: false, articles: [], pageNumber: 1, requestVersion: 0 }),
  computed: {
    isCare() { return this.isCareMe || this.$route.query.sw === 'care' },
    typeIndex() { const value = this.$route.query.sw; return value && value !== 'care' ? value : '' },
    needsLogin() { return this.isCare && !this.$store.getters.loginIn }
  },
  watch: { '$route.query.sw': 'reset', isCareMe: 'reset' },
  mounted() { this.getArticles() },
  beforeDestroy() { this.requestVersion += 1 },
  methods: {
    reset() { this.requestVersion += 1; this.articles = []; this.pageNumber = 1; this.noData = false; this.error = false; this.loading = false; this.getArticles() },
    load() { if (!this.loading && !this.noData && !this.error) this.getArticles() },
    async getArticles() {
      if (this.loading || this.needsLogin) return
      const version = ++this.requestVersion
      this.loading = true; this.error = false
      try {
        const result = await getArticles(this.isCare, this.typeIndex, { pageSize: 6, pageNumber: this.pageNumber, sort: 'desc' })
        if (version !== this.requestVersion) return
        if (!result.data || result.data.code !== 0 || !Array.isArray(result.data.data)) throw new Error('Invalid articles response')
        const entries = result.data.data
        this.articles = this.articles.concat(entries)
        this.pageNumber += 1
        this.noData = entries.length < 6
      } catch (error) { if (version === this.requestVersion) this.error = true }
      finally { if (version === this.requestVersion) this.loading = false }
    }
  }
}
</script>
<style scoped>
.discussion-feed { background: var(--surface); border: 1px solid var(--border); border-radius: 6px; overflow: hidden; }
.feed-toolbar { display: flex; justify-content: space-between; align-items: center; gap: 12px; padding: 14px 20px; border-bottom: 1px solid var(--border); background: var(--surface-muted); font-size: 13px; font-weight: 600; }
.feed-toolbar i { margin-right: 6px; color: var(--text-muted); font-size: 16px; }
.feed-sort { font-size: 12px; font-weight: 400; color: var(--text-muted); white-space: nowrap; }
.feed-skeleton { padding: 24px 20px; } .feed-skeleton .el-skeleton + .el-skeleton { margin-top: 36px; }
.feed-end { border-top: 1px solid var(--border-subtle); text-align: center; padding: 20px; font-size: 12px; color: var(--text-muted); }
@media (max-width: 600px) { .feed-toolbar { padding: 12px 16px; } .feed-sort { font-size: 11px; } }
</style>
