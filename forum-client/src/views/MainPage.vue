<template>
  <div data-title="鸭鸭论坛" style="margin:60px auto 10px">
    <el-container>

      <!--导航栏-->
      <nav-type/>

      <!--文章-->
      <el-main>
        <article-page></article-page>
      </el-main>

      <!--侧边栏-->
      <el-aside>
        <card-list/>
        <card-msg/>
        <card-hot/>
      </el-aside>

    </el-container>
  </div>
</template>

<script>
import ArticlePage from '@/components/ArticlePage'
import CardMsg from '@/components/card/CardMsg'
import CardHot from "@/components/card/CardHot";
import CardList from "@/components/card/CardList";
import NavType from "@/components/card/NavType";

export default {
  name: 'MainPage',
  components: {ArticlePage,CardMsg,CardHot,CardList,NavType}
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
