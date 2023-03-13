<template>
  <scroll-page :loading="loading" :offset="offset" :no-data="noData" v-on:load="load">
    <article-item v-for="a in articles" :key="a.id" v-bind="a"></article-item>
  </scroll-page>
</template>

<script>
import ArticleItem from '@/components/ArticleItem'
import ScrollPage from '@/components/ScrollPage'
import {getArticles} from '@/api/index'
import bus from '../assets/js/bus'

export default {
  name: "ArticleScrollPage",
  props: {
    offset: {
      type: Number,
      default: 100
    },
    isCareMe: {
      type: Boolean,
      default: false
    }
  },
  mounted() {
    if (this.$route.query.sw === 'care'){
      this.isCare = true
    }else {
      this.typeIndex = this.$route.query.sw
    }
    this.getArticles()
    bus.$emit('activeNav',this.$route.query.sw)
    bus.$on('switchType',index=>{
      this.noData=false
      this.articles=[]
      this.innerPage={
        pageSize: 6,
        pageNumber: 1,
        sort: 'desc'
      }
      if (index === 'care'){
        this.isCare = true
        this.typeIndex = ''
      }else if (index === 'all'){
        this.isCare = false
        this.typeIndex = ''
      }else {
        this.isCare = false
        this.typeIndex = index
      }
      this.getArticles()
    })
  },
  data() {
    return {
      loading: false,
      noData: false, //是否还有后续数据
      innerPage: {
        pageSize: 6,
        pageNumber: 1,
        sort: 'desc'
      },
      isCare: this.isCareMe,
      typeIndex:'',
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

      getArticles(that.isCare, that.typeIndex,that.innerPage).then(res => {
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
  },
  beforeDestroy() {
    bus.$off('switchType')
  }

}
</script>

<style scoped>
.el-card {
  border-radius: 0;
}
/*每个帖子的间隔*/
.el-card:not(:first-child) {
  margin-top: 5px;

}
</style>