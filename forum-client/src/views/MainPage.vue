<template>
  <div data-title="codebase" style="margin:60px auto 10px">
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

/*主窗口*/
.el-container {
  padding-top: 60px;
  width: 80vw;
  padding-bottom: 80px;
}

.el-aside {
  /*width: 260px;*/
}

.el-main {
  padding: 0;
  margin: 0 50px;
  min-width: 500px;
  line-height: 16px;
}

.el-card {
  border-radius: 5px;
}

.el-card:not(:first-child) {
  margin-top: 20px;
}
</style>
