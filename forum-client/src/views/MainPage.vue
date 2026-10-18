<template>
  <div class="community-page">
    <div class="page-heading">
      <div><div class="page-eyebrow"><i class="el-icon-chat-dot-square"></i> CodeBase / 社区</div><h1>社区讨论</h1><p>分享经验、提出问题，与开发者一起进步。</p></div>
      <router-link to="/write" class="button-primary"><i class="el-icon-plus"></i> 发起讨论</router-link>
    </div>
    <div class="community-layout">
      <aside class="community-navigation"><nav-type /></aside>
      <main class="discussion-main">
        <div class="mobile-toolbar"><el-button size="small" icon="el-icon-menu" @click="showLeft = !showLeft" :aria-expanded="String(showLeft)">浏览分类</el-button><el-button size="small" icon="el-icon-data-line" @click="showCard = showCard ? '' : 'hot'">社区看板</el-button></div>
        <div v-if="showLeft" class="mobile-categories"><nav-type @selected="showLeft = false" /></div>
        <article-page />
      </main>
      <aside class="community-aside"><div class="aside-label">社区看板</div><card-msg /><card-hot /><card-list /><push-art /></aside>
    </div>
    <el-drawer title="社区看板" :visible="!!showCard" @close="showCard = ''" size="min(360px, 100vw)" :append-to-body="true"><div class="mobile-board"><card-msg /><card-hot /><card-list /><push-art /></div></el-drawer>
  </div>
</template>

<script>
import ArticlePage from '@/components/article/ArticlePage'
import CardMsg from '@/components/card/CardMsg'
import CardHot from "@/components/card/CardHot";
import CardList from "@/components/card/CardList";
import PushArt from "@/components/card/PushArt";
import NavType from "@/components/card/NavType";

export default {
  name: 'MainPage',
  components: {ArticlePage,CardMsg,CardHot,CardList,NavType,PushArt},
  data() {
    return {
      showLeft: false,
      showCard: '',
      isMobile: window.innerWidth <= 900
    };
  },
  computed: {
    cardTitle() {
      const map = { list: '积分榜', msg: '公告', hot: '热帖', push: '猜你喜欢' };
      return map[this.showCard] || '';
    }
  },
  mounted() {
    window.addEventListener('resize', this.onResize);
    this.onResize();
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize);
  },
  methods: {
    onResize() {
      this.isMobile = window.innerWidth <= 900;
      if (!this.isMobile) {
        this.showLeft = false;
        this.showCard = '';
      }
    }
  }
}
</script>

<style scoped>

.community-page { max-width: 1360px; margin: 0 auto; padding: 32px 32px 48px; }
.community-layout { display: grid; grid-template-columns: 192px minmax(0, 1fr) 280px; gap: 24px; align-items: start; }
.community-navigation { position: sticky; top: 88px; min-width: 0; }
.discussion-main { min-width: 0; }
.aside-label { color: var(--text-muted); font-size: 12px; font-weight: 600; margin-bottom: 12px; }
.community-aside { min-width: 0; } .community-aside > .el-card + .el-card, .mobile-board > .el-card + .el-card { margin-top: 16px; }
.mobile-toolbar, .mobile-categories { display: none; }
.mobile-board { padding: 0 16px 24px; }
@media (max-width: 1150px) { .community-layout { grid-template-columns: 172px minmax(0, 1fr) 250px; gap: 20px; } .community-page { padding: 28px 24px 40px; } }
@media (max-width: 1000px) { .community-layout { grid-template-columns: 172px minmax(0, 1fr); } .community-aside { display: none; } .mobile-toolbar { display: flex; margin-bottom: 12px; } .mobile-toolbar > .el-button:first-child { display: none; } }
@media (max-width: 700px) { .community-page { padding: 24px 16px 32px; } .community-layout { display: block; } .community-navigation { display: none; } .mobile-toolbar > .el-button:first-child { display: inline-block; } .mobile-categories { display: block; margin-bottom: 12px; padding: 12px; background: var(--surface); border: 1px solid var(--border); border-radius: 6px; } }

</style>
