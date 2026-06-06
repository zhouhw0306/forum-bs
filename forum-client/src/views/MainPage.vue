<template>
  <div data-title="codebase" class="me-main-wrapper">
    <el-container>
      <!-- 移动端遮罩 -->
      <div v-if="showLeft || showCard" class="mobile-overlay" @click="showLeft = false; showCard = ''"></div>

      <!-- 左侧导航 -->
      <div class="left-nav" :class="{ 'mobile-show': showLeft }">
        <nav-type />
      </div>

      <!-- 文章列表 -->
      <el-main :class="{ 'full-width': !showCard }">
        <!-- 移动端切换按钮 -->
        <div class="mobile-toolbar">
          <el-button size="small" @click="showLeft = !showLeft">
            <i :class="showLeft ? 'el-icon-close' : 'el-icon-menu'"></i> 分类
          </el-button>
          <el-button size="small" @click="showCard = showCard === 'list' ? '' : 'list'">
            <i class="el-icon-trophy"></i> 积分榜
          </el-button>
          <el-button size="small" @click="showCard = showCard === 'msg' ? '' : 'msg'">
            <i class="el-icon-bell"></i> 公告
          </el-button>
          <el-button size="small" @click="showCard = showCard === 'hot' ? '' : 'hot'">
            <i class="el-icon-s-data"></i> 热帖
          </el-button>
          <el-button size="small" @click="showCard = showCard === 'push' ? '' : 'push'">
            <i class="el-icon-present"></i> 猜你喜欢
          </el-button>
        </div>
        <article-page></article-page>
      </el-main>

      <!-- 右侧边栏（宽屏） -->
      <el-aside class="desktop-aside">
        <card-list/>
        <card-msg/>
        <card-hot/>
        <push-art/>
      </el-aside>

      <!-- 移动端弹出面板 -->
      <div v-if="showCard" class="mobile-panel" :class="{ 'mobile-show': !!showCard }">
        <div class="mobile-panel-head">
          <span>{{ cardTitle }}</span>
          <i class="el-icon-close" @click="showCard = ''"></i>
        </div>
        <div class="mobile-panel-body">
          <card-list v-if="showCard === 'list'"/>
          <card-msg  v-if="showCard === 'msg'"/>
          <card-hot  v-if="showCard === 'hot'"/>
          <push-art  v-if="showCard === 'push'"/>
        </div>
      </div>

    </el-container>
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
.me-main-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 80px 20px 60px;
}
.el-container {
  padding-bottom: 40px;
}
.el-main {
  padding: 0 40px;
  flex: 1;
  min-width: 0;
  max-width: 900px;
  line-height: 16px;
}
.el-aside, .desktop-aside {
  width: 260px;
  flex-shrink: 0;
}
@media (max-width: 900px) {
  .desktop-aside { display: none; }
}

/* 左侧导航 */
.left-nav {
  width: 160px;
  flex-shrink: 0;
}
.left-nav >>> .el-menu-vertical-demo {
  position: sticky;
  top: 80px;
  width: 160px;
}

/* 大屏 */
@media (min-width: 1600px) {
  .me-main-wrapper { padding-left: 5vw; padding-right: 5vw; }
}

.el-card { border-radius: 5px; }
.el-card:not(:first-child) { margin-top: 20px; }

/* 桌面端：el-main 左侧留出 nav 的空间 */
@media (min-width: 901px) {
  .el-main { margin-left: 0; }
}

/* ---- 响应式 ---- */
.mobile-toolbar {
  display: none;
  margin-bottom: 12px;
  padding: 8px 0;
  gap: 10px;
}
.mobile-overlay {
  display: none;
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,.3);
  z-index: 1400;
}

@media (max-width: 900px) {
  .mobile-toolbar { display: flex; }
  .mobile-overlay { display: block; }

  .left-nav {
    position: fixed;
    top: 60px; left: -240px; bottom: 0;
    width: 240px;
    z-index: 1450;
    background: #fff;
    overflow-y: auto;
    transition: left .25s;
    box-shadow: 2px 0 12px rgba(0,0,0,.1);
  }
  .left-nav.mobile-show { left: 0; }
  .left-nav >>> .el-menu-vertical-demo { position: static; width: 100%; }

  .desktop-aside { display: none; }
  .el-aside { display: none; }

  .mobile-panel {
    position: fixed;
    top: 0; right: -100%; bottom: 0;
    width: 90%;
    max-width: 340px;
    z-index: 1450;
    background: #fff;
    box-shadow: -2px 0 16px rgba(0,0,0,.15);
    transition: right .3s;
    display: flex;
    flex-direction: column;
  }
  .mobile-panel.mobile-show { right: 0; }
  .mobile-panel-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 18px;
    font-size: 16px;
    font-weight: 600;
    color: #1a1a2e;
    border-bottom: 1px solid #f0f0f0;
  }
  .mobile-panel-head i { font-size: 20px; color: #909399; cursor: pointer; }
  .mobile-panel-body {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
  }

  .el-main {
    margin-left: 0 !important;
    padding: 0 10px;
    width: 100%;
  }
}
</style>
