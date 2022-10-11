<template>
  <scroll-page :loading="loading" :offset="offset" :no-data="noData" v-on:load="load">
    <article-item v-for="a in articles" :key="a.id" v-bind="a"></article-item>
  </scroll-page>
</template>

<script>
import ArticleItem from '@/components/ArticleItem'
import ScrollPage from '@/components/ScrollPage'
import {getArticles} from '@/api/index'

export default {
  name: "ArticleScrollPage",
  props: {
    offset: {
      type: Number,
      default: 100
    },
    page: {
      type: Object,
      default() {
        return {}
      }
    },
    query: {
      type: Object,
      isCareMe: Boolean,
      default() {
        return {}
      }
    }
  },
  watch: {
    'query': {
      handler() {
        this.noData = false
        this.articles = []
        this.innerPage.pageNumber = 1
        this.getArticles()
      },
      deep: true
    },
    'page': {
      handler() {
        this.noData = false
        this.articles = []
        this.innerPage = this.page
        this.getArticles()
      },
      deep: true
    }
  },
  created() {
    this.getArticles()
  },
  data() {
    return {
      loading: false,
      noData: false,
      innerPage: {
        pageSize: 6,
        pageNumber: 1,
        sort: 'desc'
      },
      articles: []
    }
  },
  methods: {
    load() {
      this.getArticles()
    },
    view(id) {
      this.$router.push({path: `/view/${id}`})
    },
    getArticles() {
      let that = this
      that.loading = true

      getArticles(that.query, that.innerPage).then(res => {
        let newArticles = res.data.data
        if (newArticles && newArticles.length > 0) {
          that.innerPage.pageNumber += 1
          that.articles = that.articles.concat(newArticles)
        } else {
          that.noData = true
        }

      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '文章加载失败!', showClose: true})
        }
      }).finally(() => {
        that.loading = false
      })

    }
  },
  components: {
    'article-item': ArticleItem,
    'scroll-page': ScrollPage
  }

}
</script>

<style scoped>
.el-card {
  border-radius: 0;
}
/*每个帖子的间隔*/
.el-card:not(:first-child) {
  margin-top: 15px;

}
</style>