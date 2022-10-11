<template>
  <div v-title data-title="鸭鸭论坛" style="margin:10px auto">
    <el-container>

      <!--我关注的文章-->
      <el-main class="me-articles">
        <article-page :query={isCareMe:true}></article-page>
      </el-main>

      <!--侧边栏-->
      <el-aside>
        <card-me class="card"></card-me>
      </el-aside>

    </el-container>
  </div>
</template>

<script>
import ArticlePage from '@/components/ArticlePage'
import CardMe from '@/components/card/CardMe'

export default {
  name: 'SecondPage',
  components: {ArticlePage,CardMe}
}
</script>

<style scoped>

/*主窗口*/
.el-container {
  padding-top: 100px;
  paddingn-left: 500px;
  width: 1000px;
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
  /*background-color: white;*/
  /*height: 300px;*/
  border-radius: 5px;
}

.el-card:not(:first-child) {
  margin-top: 20px;
}
</style>
