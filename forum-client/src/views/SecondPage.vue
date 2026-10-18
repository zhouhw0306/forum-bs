<template>
  <div class="following-page page-container">
    <div class="page-heading"><div><div class="page-eyebrow">CodeBase / 关注</div><h1>关注动态</h1><p>不错过你关注的开发者分享的新内容。</p></div><router-link :to="{ path: '/searchPage', query: { activeName: 'userTable' } }" class="following-discover"><i class="el-icon-discover"></i> 发现开发者</router-link></div>
    <div class="following-layout">
      <aside class="following-profile" aria-label="我的个人信息"><card-me /></aside>
      <main class="following-feed"><article-page :is-care-me="true" /></main>
    </div>
  </div>
</template>

<script>
import ArticlePage from '@/components/article/ArticlePage'
import CardMe from '@/components/card/CardMe'
export default { name: 'SecondPage', components: { ArticlePage, CardMe } }
</script>

<style scoped>
.following-page { max-width: 1200px; }
.following-layout { display: grid; grid-template-columns: minmax(0, 1fr) 292px; grid-template-areas: 'feed profile'; gap: 24px; align-items: start; }
.following-feed { grid-area: feed; min-width: 0; }
.following-profile { grid-area: profile; position: sticky; top: 88px; min-width: 0; }
.following-discover { display: inline-flex; align-items: center; justify-content: center; gap: 6px; padding: 7px 12px; border: 1px solid var(--border); border-radius: 6px; background: var(--surface); color: var(--text); font-size: 12px; white-space: nowrap; }
.following-discover:hover { background: var(--hover); border-color: #afb8c1; }
@media (max-width: 900px) { .following-layout { grid-template-columns: minmax(0, 1fr); grid-template-areas: 'profile' 'feed'; gap: 20px; } .following-profile { position: static; } }
@media (max-width: 600px) { .following-discover { padding: 6px 10px; font-size: 11px; } }
</style>
