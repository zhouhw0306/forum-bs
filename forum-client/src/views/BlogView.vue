<template>
  <div style="margin: 100px auto 140px;min-width: 1000px">
  <div class="me-view-body" :data-title="title">
    <el-container class="me-view-container">
      <el-main>
        <div @click="back">
          <a><i class="el-icon-back"></i> 返回</a>
        </div>
        <div class="me-view-card">
          <h1 class="me-view-title">{{article.title}}</h1>
          <div class="me-view-author">
            <a class="">
              <img class="me-view-picture" :src="attachImageUrl(article.author.avatar)"/>
            </a>
            <div class="me-view-info">
              <span>{{article.author.username}}</span><button v-if="this.article.author.id !== this.$store.getters.userId" @click="updateFollow" :class="{btnOf : true,follow : !isFollow}">{{follow}}</button>
              <div class="me-view-meta">
                <span style="padding-right: 20px">发布时间:   {{article.createTime}}</span>
                <span style="padding-right: 20px">阅读   {{article.viewCount}}</span>
                <span>评论   {{article.commentCount}}</span>
              </div>

            </div>
            <el-button
              v-if="this.article.author.id === this.$store.getters.userId"
              @click="editArticle()"
              style="position: relative;left: 30%;"
              size="mini"
              round
              icon="el-icon-edit">编辑</el-button>
          </div>
          <div class="me-view-content">
            <markdown-editor style="box-shadow: none" :editor=editor></markdown-editor>
          </div>

          <div class="me-view-end">
            <el-alert
              title="帖子End..."
              type="success"
              center
              :closable="false">
            </el-alert>
          </div>


          <el-divider content-position="center">评论区</el-divider>
          <div class="me-view-comment">
            <div class="me-view-comment-write">
              <el-row :gutter="20">
                <el-col :span="2">
                  <a class="">
                    <img class="me-view-picture" :src="attachImageUrl(avatar)"/>
                  </a>
                </el-col>
                <el-col :span="22">
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 2}"
                    placeholder="你的评论..."
                    class="me-view-comment-text"
                    v-model="comment.content"
                    resize="none">
                  </el-input>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="2" :offset="22">
                  <el-button type="text" @click="pushComment()">评论</el-button>
                </el-col>
              </el-row>
            </div>

            <div class="me-view-comment-title">
              <span>{{article.commentCount}} 条评论</span>
            </div>

            <commment-item
              v-for="(c,index) in comments"
              :comment="c"
              :articleId="article.id"
              :index="index"
              :rootCommentCounts="comments.length"
              @commentCountsIncrement="commentCountsIncrement"
              :key="c.id">
            </commment-item>

          </div>
        </div>
      </el-main>
    </el-container>
  </div>
  <div class="infoCard">
    <span>帖子信息</span>
    <el-button v-if="!hasFavour" @click="updateFavour" style="float: right" size="mini" icon="el-icon-star-off" circle></el-button>
    <el-button v-else @click="updateFavour" style="float: right" type="danger" size="mini" icon="el-icon-star-off" circle></el-button>
    <el-divider style="margin: 15px 0"></el-divider>
    <div class="me-view-tag">
      作者：
      {{article.author.username}}
    </div>
    <div class="me-view-tag">
      标签：
      <el-tag size="small" v-for="t in article.tags" :key="t.id" :type="type[t.id%5]" style="margin-right: 10px">{{t.tagName}}</el-tag>
    </div>
    <div class="me-view-tag">
      文章分类：
      <el-tag size="small" @click="tagOrCategory('category', article.category.id)">{{article.categoryName}}</el-tag>
    </div>
  </div>
  </div>
</template>

<script>
  import {mixin} from "@/mixins"
  import MarkdownEditor from '@/components/MarkdownEditor'
  import CommmentItem from '@/components/CommentItem'
  import {
    getTypeById,
    getCommentsByArticle,
    pushComment,
    getArticle,
    getAuthorById,
    getTags,
    addViewCount,
    isFollow, isFavour,
    addFollow, removeFollow, articleFavour
  } from '@/api/index'

  export default {
    name: 'BlogView',
    mixins: [mixin],
    watch: {
      '$route': 'getArticle'
    },
    data() {
      return {
        type:['','success','info','warning','danger'],
        article: {
          id: '',
          title: '',
          commentCount: 0,
          viewCount: 0,
          author: {},
          tags: [],
          categoryId:'',
          categoryName:'',
          createDate: '',
          content:'',
        },
        editor: {
          value: '',
          toolbarsFlag: false,
          subfield: false,
          defaultOpen: 'preview'
        },
        comments: [],
        comment: {
          content: ''
        },
        follow: '',
        isFollow: false, //是否关注作者
        hasFavour: false
      }
    },
    mounted() {
      window.scrollTo(0,0);
      //前端点进来的
      if (this.$route.query.article && this.$route.query.article.id){
        this.article = this.$route.query.article
        this.editor.value = this.article.content
        //获取帖子类型和评论
        this.getTypeById()
        this.getCommentsByArticle()
        //判断是否关注作者
        this.InitIsFollow()
        //添加阅读量
        addViewCount(this.$route.params.id)
      }else {
        //获得帖子及作者和标签
        this.getArticle()
      }
    },
    computed: {
      avatar() {
        let avatar = this.$store.getters.avatar
        if (avatar !== null) {
          return avatar
        }
        return '/avatarImages/default_user.jpg'
      },
      title() {
        return `${this.article.title} - 帖子 - school`
      }
    },
    methods: {
      back(){
        this.$router.go(-1)
      },
      //更新关注关系
      updateFollow(){
        if (!this.$store.getters.loginIn){
          this.$message.error('请先登录')
          return
        }
        let params = new URLSearchParams()
        params.append('userId',this.$store.getters.userId)
        params.append('authorId',this.article.author.id)
        //取消关注
        if(this.isFollow){
          removeFollow(params).then(res => {
            if (res.code === 0){
              this.isFollow = false
              this.follow = '+关注'
              this.$message.success('取消成功')
            }else {
              this.$message.error('取消失败')
            }
          })
        }else {
        //关注作者
          addFollow(params).then(res => {
            if (res.code === 0){
              this.isFollow = true
              this.follow = '已关注'
              this.$message.success('关注成功')
            }else {
              this.$message.error('关注失败')
            }
          })
        }
      },
      //更新收藏关系
      updateFavour(){
        let params = new URLSearchParams();
        params.append("targetId", this.article.id);
        articleFavour(params).then(res => {
          if (res.data === 1){
            this.hasFavour = true
            this.$message.success('收藏成功')
          }else if(res.data === -1){
            this.hasFavour = false
            this.$message.success('取消收藏成功')
          }else{
            this.$message.error(res.msg)
          }
        }).catch(err => this.$message.error(err.data.msg))
      },
      //判断是否关注文章作者
      InitIsFollow(){
        let userId = this.$store.getters.userId
        //未登录直接返回
        if (userId === '' || userId === null){
          this.follow='+关注'
          this.isFollow=false
          return
        }
        let params = new URLSearchParams()
        params.append('userId',userId)
        params.append('authorId',this.article.author.id)
        // 是否关注作者
        isFollow(params).then(res => {
          this.isFollow = res.data
          res.data ? this.follow='已关注' : this.follow='+关注'
        }).catch( err => {this.$message.error(err.msg)})
        // 判断是否收藏
        isFavour(this.article.id).then(res => {
          this.hasFavour = res.data
        }).catch( err => {this.$message.error(err)})
      },
      tagOrCategory(type, id) {
        this.$router.push({path: `/${type}/${id}`})
      },
      editArticle() {
        this.$router.push({path: `/write/${this.article.id}`})
      },
      //获得文章
      async getArticle() {
        // 绕过首页获取帖子
        await getArticle(this.$route.params.id).then(res => {
          this.article = res.data
          this.editor.value = this.article.content
        }).catch(err => {
          this.$message({type: 'error', message: `文章加载失败${err.msg}`, showClose: true})
        })
        // 获取作者
        await getAuthorById(this.article.userId).then(res => {
          if (res.code === 0){
            this.$set(this.article,'author',res.data)
          }
        }).catch(err => {this.$message({type: 'error', message: `作者加载失败!${err.msg}`, showClose: true})})
        //获取帖子标签、类型和评论
        this.InitTags()
        this.getTypeById()
        this.getCommentsByArticle()
        //判断是否关注作者
        this.InitIsFollow()
        //添加阅读量
        addViewCount(this.$route.params.id)
      },
      //获得文章标签
      InitTags(){
        let params = new URLSearchParams()
        params.append("articleId",this.article.id)
        getTags(params).then(res => {
          if (res.code === 0){
            this.$set(this.article,'tags',res.data)
          }
        }).catch(err => {this.$message({type: 'error', message: '标签加载失败!', showClose: true})})
      },
      //转换文章分类
      getTypeById() {
        if (this.article.categoryId){
          let params = new URLSearchParams()
          params.append("id",this.article.categoryId)
          getTypeById(params).then(res=>{
            this.$set(this.article,'categoryName',res.data)
          })
        }
      },
      //发布评论
      pushComment() {
        if (!this.comment.content.trim()) {
          this.$message({type: 'error', message: '评论不能为空', showClose: true})
          return;
        }
        if (!this.$store.getters.loginIn){
          this.$message({type: 'error', message: '请先登录', showClose: true})
          return;
        }
        let params = new URLSearchParams();
        params.append("content",this.comment.content)
        params.append("articleId",this.article.id)
        params.append("level","0")
        pushComment(params).then(res => {
          if (res.code===0){
            this.$message({type: 'success', message: '评论成功', showClose: true})
            this.comments.unshift(res.data)
            this.commentCountsIncrement()
            this.comment.content = ''
          }else{
            this.$message({type: 'error', message: `评论失败${res.msg}`, showClose: true})
          }
        }).catch(err => {
          this.$message({type: 'error', message: `评论失败${err.msg}`, showClose: true})
        })
      },
      //得到文章评论
      getCommentsByArticle() {
        let params = new URLSearchParams();
        params.append('articleId',this.article.id)
        getCommentsByArticle(params).then(res => {
          this.comments = res.data
        }).catch(err => {
          if (err !== 'error') {
            this.$message({type: 'error', message: '评论加载失败', showClose: true})
          }
        })
      },
      commentCountsIncrement() {
        this.article.commentCount += 1
      }
    },
    components: {
      'markdown-editor': MarkdownEditor,
      CommmentItem
    },
  }
</script>

<style>
  .me-view-body {
    background-color: white;
    border-radius: 10px;
    display: inline-block;
  }

  .me-view-container {
    width: 700px;
  }

  .el-main {
    overflow: hidden;

  }

  .me-view-title {
    font-size: 34px;
    font-weight: 700;
    line-height: 1.3;
    margin-top: 10px;
  }

  .me-view-author {
    margin: 20px 0 10px;
    /*margin-top: 30px;*/
    vertical-align: middle;
  }

  .me-view-picture {
    width: 40px;
    height: 40px;
    border: 1px solid #ddd;
    border-radius: 50%;
    vertical-align: middle;
    background-color: rgb(255, 153, 195);
  }

  .me-view-info {
    display: inline-block;
    vertical-align: middle;
    margin-left: 8px;
  }

  .me-view-meta {
    font-size: 12px;
    color: #525151;
  }

  .me-view-end {
    margin-top: 20px;
  }

  .me-view-tag {
    margin-top: 20px;
    padding-left: 6px;
    border-left: 4px solid #6fcbc3;
  }

  .me-view-tag-item {
    margin: 0 4px;
  }

  .me-view-comment {
    margin-top: 60px;
  }

  .me-view-comment-title {
    font-weight: 600;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 20px;
  }

  .me-view-comment-write {
    margin-top: 20px;
  }

  .me-view-comment-text {
    font-size: 16px;
  }

  .v-show-content {
    padding: 8px 25px 15px 0px !important;
  }

  .v-note-wrapper .v-note-panel {
    box-shadow: none !important;
  }

  .v-note-wrapper .v-note-panel .v-note-show .v-show-content, .v-note-wrapper .v-note-panel .v-note-show .v-show-content-html {
    background: #fff !important;
  }
  .follow{
    background-color: #f25d8e;
    color: #fff;
    border-style: none;
  }
  .btnOf{
    border-style: none;
    border-radius: 3px;
    margin-left: 3px;
    padding: 2px 5px;
    border-radius: 15px;
  }
  .btnOf:hover{
    background-color: #dbdada;
  }
  .hljs{
    background: #f6f8fa!important;
  }
  .infoCard{
    float: right;
    margin-left: 20px;
    width: 240px;
    background-color: white;
    padding: 15px;
    position: sticky;
    top: 100px
  }
</style>
