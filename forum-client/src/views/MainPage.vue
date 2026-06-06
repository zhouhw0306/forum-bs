<template>
  <div data-title="codebase" class="me-main-wrapper" style="margin-top: 60px">
    <el-container>

      <!--导航栏-->
      <nav-type style="position: fixed"/>

      <!--文章-->
      <el-main style="margin-left: 220px">
        <article-page></article-page>
      </el-main>

      <!--侧边栏-->
      <el-aside>
        <card-list/>
        <card-msg/>
        <card-hot/>
        <push-art/>
      </el-aside>

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
  components: {ArticlePage,CardMsg,CardHot,CardList,NavType,PushArt}
}
</script>

<style scoped>

/* 外层居中，自适应宽度 */
.me-main-wrapper {
  width: 100%;
  margin: 0 auto;
}

/*主窗口：弹性布局，自适应 */
.el-container {
  padding-top: 60px;
  padding-bottom: 80px;
}

.el-main {
  padding: 0 20px;
  flex: 1;
  min-width: 0;
  line-height: 16px;
}

/* 侧边栏 */
.el-aside {
  width: 260px;
  flex-shrink: 0;
}

/* 小屏时隐藏侧边栏 */
@media (max-width: 900px) {
  .el-aside {
    display: none;
  }
  .el-main {
    padding: 0 10px;
  }
}

/* 大屏时增加侧间距 */
@media (min-width: 1600px) {
  .me-main-wrapper {
    padding: 0 5vw;
  }
}

.el-card {
  border-radius: 5px;
}

.el-card:not(:first-child) {
  margin-top: 20px;
}
</style>
