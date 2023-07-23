<template>
  <el-card class="me-area" :body-style="{ padding: '16px' }">

    <div class="me-article-header">
      <a @click="view(id)" class="me-article-title">{{title}}</a>
      <span class="me-pull-right me-article-count">
	    	<i class="el-icon-chat-dot-round"></i>&nbsp;{{commentCount}}
	    </span>
      <span class="me-pull-right me-article-count">
	    	<i class="el-icon-view"></i>&nbsp; {{viewCount}}
	    </span>
    </div>

    <div class="me-artile-description">
      {{filterHtml(contentHtml)}}
    </div>
    <div class="me-article-footer">
	  	<span class="me-article-author">
	    	<i class="el-icon-user"></i>&nbsp;发帖人: {{author.username}}
	    </span>

      <el-tag v-for="t in tags" :key="t.tagName" size="mini" :type="type[t.id%4]">{{t.tagName}}</el-tag>

      <span class="me-pull-right me-article-count">
	    	<i class="el-icon-time"></i>&nbsp;{{createTime}}
	    </span>

    </div>
  </el-card>
</template>

<script>

import {getAuthorById, getTags} from "@/api";

export default {
  name: 'ArticleItem',
  props: {
    id: String,
    categoryId : Number,
    title: String,
    commentCount: Number,
    viewCount: Number,
    content: String,
    contentHtml: String,
    createTime: String,
    userId: String,
    updateTime: String
  },
  data() {
    return {
      type:['','success','warning','danger'],
      author:{},
      tags: {}
    }
  },
  mounted() {
    this.InitAuthor(this.userId)
    this.InitTags(this.id)
  },
  methods: {
    //过滤html标签
    filterHtml(strHTML){
      let re = new RegExp('<[^<>]+>','g');
      strHTML = strHTML.replace(re ,"");
      strHTML = strHTML.replace(/<[^<>]+>/g,"");
      return strHTML
    },
    view(id) {
      let article={
        id : this.id,
        title : this.title,
        categoryId : this.categoryId,
        commentCount : this.commentCount,
        viewCount : this.viewCount,
        content : this.content,
        createTime : this.createTime,
        updateTime : this.updateTime,
        userId : this.userId,
        author : this.author,
        tags : this.tags
      }
      this.$router.push({path: `/view/${id}`,query : {article : article}})
    },
    //获得文章作者
    InitAuthor(id){
      getAuthorById(id).then(res => {
        if (res.code === 0){
          this.author=res.data
        }
      }).catch(err => {this.$message({type: 'error', message: '作者加载失败!', showClose: true})})
    },
    //获得文章标签
    InitTags(id){
      let params = new URLSearchParams()
      params.append("articleId",id)
      getTags(params).then(res => {
        if (res.code === 0){
          this.tags = res.data
        }
      }).catch(err => {this.$message({type: 'error', message: '标签加载失败!', showClose: true})})
    }
  }
}
</script>

<style>

.me-article-header {
  padding: 10px 1px;
}

.me-article-title {
  font-weight: 600;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.me-article-icon {
  padding: 3px 8px;
}

.me-article-count {
  color: #a6a6a6;
  padding-left: 14px;
  font-size: 13px;
}

.me-pull-right {
  float: right;
}

.me-artile-description {
  font-size: 13px;
  color: #8a919f;
  line-height: 24px;
  margin-bottom: 10px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.me-article-author {
  color: #a6a6a6;
  padding-right: 18px;
  font-size: 13px;
}

.el-tag {
  margin-left: 6px;
}

</style>