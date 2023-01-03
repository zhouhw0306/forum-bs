<template>

  <el-card class="me-area" :body-style="{ padding: '16px' }">

    <div class="me-article-header">

      <a @click="view(id)" class="me-article-title">{{title}}</a>
<!--      <el-button v-if="weight > 0" class="me-article-icon" type="text">置顶</el-button>-->
      <span class="me-pull-right me-article-count">
	    	<i class="el-icon-chat-dot-round"></i>&nbsp;{{commentCount}}
	    </span>
      <span class="me-pull-right me-article-count">
	    	<i class="el-icon-view"></i>&nbsp; {{viewCount}}
	    </span>
    </div>

    <div class="me-artile-description">
      {{smallContent}}
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

import {getAuthor,getTags} from "@/api";

export default {
  name: 'ArticleItem',
  props: {
    id: String,
    categoryId : Number,
    //weight: Number,
    title: String,
    commentCount: Number,
    viewCount: Number,
    content: String,
    //summary: String,
    //author: Object,
    //tags: Array,
    createTime: String,
    userId: String,
    updateTime: String
  },
  data() {
    return {
      type:['','success','warning','danger'],
      author:{},
      tags: {},
      smallContent:''
    }
  },
  mounted() {
    this.InitAuthor(this.userId)
    this.InitTags(this.id)
    if (this.content.length>100){
      this.smallContent = this.content.substring(0,99)+'......'
    }else {
      this.smallContent = this.content
    }
  },
  methods: {
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
      let params = new URLSearchParams()
      params.append("id",id)
      getAuthor(params).then(res => {
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

<style scoped>

.me-article-header {
  padding: 10px 1px;
}

.me-article-title {
  font-weight: 600;
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
  line-height: 24px;
  margin-bottom: 10px;
  /*white-space: nowrap;*/
  /*text-overflow: ellipsis;*/
  /*overflow: hidden;*/
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