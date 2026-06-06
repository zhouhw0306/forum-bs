<template>
  <div data-title="鸭鸭论坛" class="me-care-wrapper" style="margin-top: 60px">
    <el-container>

      <!--我关注的文章-->
      <el-main class="me-articles">
        <article-page :isCareMe=true></article-page>
      </el-main>

      <!--侧边栏-->
      <el-aside>
        <card-me class="card"></card-me>
      </el-aside>

    </el-container>
  </div>
</template>

<script>
import ArticlePage from '@/components/article/ArticlePage'
import CardMe from '@/components/card/CardMe'

export default {
  name: 'SecondPage',
  components: {ArticlePage,CardMe}
}
</script>

<style scoped>

/* 外层居中 */
.me-care-wrapper {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
}

/*主窗口*/
.el-container {
  padding-top: 100px;
  padding-bottom: 80px;
}

.el-aside {
  margin-left: 40px;
  width: 260px;
  flex-shrink: 0;
}

.el-main {
  padding: 0;
  flex: 1;
  min-width: 0;
  line-height: 16px;
}

@media (max-width: 900px) {
  .el-aside {
    display: none;
  }
}

.el-card {
  border-radius: 5px;
}

.el-card:not(:first-child) {
  margin-top: 20px;
}
</style>
