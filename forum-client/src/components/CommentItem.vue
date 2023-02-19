<template>
  <div class="me-view-comment-item">
    <div class="me-view-comment-author">

      <div class="me-view-info">
        <a class="">
          <el-avatar :src="attachImageUrl(comment.user.avatar)"></el-avatar>
        </a>
        <span class="me-view-nickname">{{comment.user.username}}</span>
        <span class="me-view-meta" style="margin-left: -15px">{{comment.createTime}}</span>
        <span class="me-view-meta" style="margin-left: 10px">{{rootCommentCounts - index}}楼</span>
      </div>
    </div>
    <div>
      <p class="me-view-comment-content">{{comment.content}}</p>
      <div class="me-view-comment-tools">
        <a class="me-view-comment-tool" @click="showComment(-1)">
          <i class="me-icon-comment"></i>&nbsp; 评论
        </a>
      </div>

      <div class="me-reply-list">
        <div class="me-reply-item" v-for="c in comment.childrens" :key="c.id">
          <div style="font-size: 14px">
            <span class="me-reply-user">{{c.user.username}}:&nbsp;&nbsp;</span>

            <span v-if="c.level == 2" class="me-reply-user">@{{c.toUser.username}} </span>

            <span>{{c.content}}</span>
          </div>
          <div class="me-view-meta">
            <span style="padding-right: 10px">{{c.createTime}}</span>
            <a class="me-view-comment-tool" @click="showComment(c.id, c.user)">
              <i class="me-icon-comment"></i>&nbsp;回复
            </a>
          </div>

        </div>
        <!--回复框-->
        <div class="me-view-comment-write" v-show="commentShow">

          <el-input
            v-model="reply.content"
            type="input"
            style="width: 90%"
            :placeholder="placeholder"
            class="me-view-comment-text"
            resize="none">
          </el-input>

          <el-button style="margin-left: 8px" @click="pushComment()" type="text">评论</el-button>

        </div>

      </div>

    </div>
  </div>
</template>

<script>
import {pushComment} from '@/api/index'
import {mixin} from "@/mixins";

export default {
  name: "CommentItem",
  mixins: [mixin],
  props: {
    articleId: String,
    comment: Object,
    index: Number,
    rootCommentCounts: Number
  },
  data() {
    return {
      placeholder: '你的评论...',
      commentShow: false,
      commentShowIndex: '',
      reply: this.getEmptyReply()
    }
  },
  methods: {
    showComment(commentShowIndex, toUser) {
      this.reply = this.getEmptyReply()

      if (this.commentShowIndex !== commentShowIndex) {

        if (toUser) {
          this.placeholder = `@${toUser.username} `
          this.reply.toUser = toUser
        } else {
          this.placeholder = '你的评论...'
        }

        this.commentShow = true
        this.commentShowIndex = commentShowIndex
      } else {
        this.commentShow = false
        this.commentShowIndex = ''
      }
    },
    pushComment() {
      if (!this.$store.getters.loginIn){
        this.$message({type: 'error', message: '请先登录', showClose: true})
        return;
      }
      if (!this.reply.content) {
        this.$message({type: 'error', message: '内容不能为空', showClose: true})
        return;
      }

      let params = new URLSearchParams();
      params.append("content",this.reply.content)
      params.append("articleId",this.reply.article.id)
      params.append("authorId",this.$store.getters.userId)
      params.append("parentId",this.reply.parent.id)
      if (this.reply.toUser){
        params.append("toUid",this.reply.toUser.id)
        params.append("level","2")
      }else {
        params.append("level","1")
      }
      pushComment(params).then(data => {
        this.$message({type: 'success', message: '评论成功', showClose: true})
        //this.$router.go(0)
        if(!this.comment.childrens){
          this.comment.childrens = []
        }
        this.comment.childrens.unshift(data.data)
        this.$emit('commentCountsIncrement')
        this.showComment(this.commentShowIndex)
      }).catch(error => {
        if (error !== 'error') {
          this.$message({type: 'error', message: '评论失败', showClose: true})
        }
      })

    },
    getEmptyReply() {
      return {
        article: {
          id: this.articleId
        },
        parent: {
          id: this.comment.id
        },
        toUser: '',
        content: ''
      }
    }
  }
}
</script>

<style>
  .me-view-tag-item {
    margin: 0 4px;
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

  .me-view-comment-item {
    margin-top: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
  }

  .me-view-comment-author {
    margin: 10px 0;
    vertical-align: middle;
  }

  .me-view-nickname {
    font-size: 14px;
    vertical-align: top;
    margin-left: 10px;
  }

  .me-view-comment-content {
    line-height: 1.5;
    margin-left: 20px;
  }

  .me-view-comment-tools {
    margin-top: 4px;
    margin-bottom: 10px;
  }

  .me-view-comment-tool {
    font-size: 13px;
    color: #a6a6a6;
    padding-right: 14px;
  }

  .v-note-wrapper .v-note-panel .v-note-show .v-show-content, .v-note-wrapper .v-note-panel .v-note-show .v-show-content-html {
    background: #fff !important;
  }

  .me-reply-list {
    padding-left: 16px;
    border-left: 4px solid #87bd71;
  }

  .me-reply-item {
    margin-bottom: 8px;
    border-bottom: 1px solid #f0f0f0;
  }

  .me-reply-user {
    color: #78b6f7;
  }
</style>
