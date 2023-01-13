<template>
  <div v-title data-title="鸭鸭论坛" style="margin:60px auto 10px">
    <el-container>

      <!--文章-->
      <el-main class="me-articles">
        <article-page></article-page>
      </el-main>

      <!--侧边栏-->
      <el-aside>
        <card-list class="card"></card-list>
        <card-msg class="card"></card-msg>
        <card-hot class="card"></card-hot>
      </el-aside>

    </el-container>
  </div>
</template>

<script>
import ArticlePage from '@/components/ArticlePage'
import CardMsg from '@/components/card/CardMsg'
import CardHot from "@/components/card/CardHot";
import CardList from "@/components/card/CardList";

export default {
  name: 'MainPage',
  components: {ArticlePage,CardMsg,CardHot,CardList}
}
</script>

<style scoped>

/*主窗口*/
.el-container {
  padding-top: 60px;
  width: 65vw;
  padding-bottom: 80px;
}

.el-aside {
  margin-left: 60px;
  width: 260px;
}

.el-main {
  padding: 0px;
  line-height: 16px;
}

.el-card {
  border-radius: 5px;
}

.el-card:not(:first-child) {
  margin-top: 20px;
}

.me-articles{
  min-width: 500px;
}
</style>
