<template>
  <div class="comment-item">
    <!-- 评论头部 -->
    <div class="comment-header">
      <el-avatar :src="attachImageUrl(comment.user.avatar)" :size="36" class="comment-avatar" />
      <div class="comment-info">
        <div class="comment-author">
          <span class="author-name">{{ comment.user.username }}</span>
          <el-tag
            v-if="comment.user.id === articleAuthorId"
            size="mini"
            type="warning"
            class="author-tag"
          >
            作者
          </el-tag>
        </div>
        <div class="comment-meta">
          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
          <span class="comment-floor">第{{ rootCommentCounts - index }}楼</span>
        </div>
      </div>
      <div class="comment-actions">
        <el-button
          type="text"
          size="mini"
          class="action-btn"
          @click="toggleLike(comment.id)"
        >
          <i
            :class="['el-icon-thumb', { 'liked': isLiked }]"
            :style="{ color: isLiked ? '#f56c6c' : '#909399' }"
          ></i>
          {{ comment.likeCount || 0 }}
        </el-button>
        <el-button
          type="text"
          size="mini"
          class="action-btn"
          @click="showComment(-1)"
        >
          <i class="el-icon-chat-line-round"></i>
          回复
        </el-button>
      </div>
    </div>

    <!-- 评论内容 -->
    <div class="comment-content">
      <div
        class="content-text"
        v-hljs
        v-html="marked(comment.content)"
      ></div>
    </div>

    <!-- 评论工具栏 -->
    <div class="comment-toolbar">
      <el-button
        v-show="comment.children && comment.children?.length > 0"
        type="text"
        size="mini"
        class="toolbar-btn"
        @click="toggleExtension"
      >
        <i :class="extension ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        {{ extension ? '收起' : '展开' }}({{ comment.children?.length }})
      </el-button>
    </div>

    <!-- 子评论列表 -->
    <transition-group
      v-if="extension && comment.children && comment.children?.length > 0"
      name="reply-list"
      tag="div"
      class="replies-container"
    >
      <div
        v-for="reply in comment.children"
        :key="reply.id"
        class="reply-item"
      >
        <div class="reply-header">
          <el-avatar :src="attachImageUrl(reply.user.avatar)" :size="32" class="reply-avatar" />
          <div class="reply-info">
            <div class="reply-author">
              <span class="author-name">{{ reply.user.username }}</span>
              <el-tag
                v-if="reply.user.id === articleAuthorId"
                size="mini"
                type="warning"
                class="author-tag"
              >
                作者
              </el-tag>
              <span v-if="reply.level === '2'" class="reply-to">@{{ reply.toUser.username }}</span>
            </div>
            <div class="reply-meta">
              <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
            </div>
          </div>
          <div class="reply-actions">
            <el-button
              type="text"
              size="mini"
              class="action-btn"
              @click="toggleReplyLike(reply.id)"
            >
              <i
                :class="['el-icon-thumb', { 'liked': isReplyLiked(reply.id) }]"
                :style="{ color: isReplyLiked(reply.id) ? '#f56c6c' : '#909399' }"
              ></i>
              {{ reply.likeCount || 0 }}
            </el-button>
            <el-button
              type="text"
              size="mini"
              class="action-btn"
              @click="showComment(reply.id, reply.user)"
            >
              <i class="el-icon-chat-line-round"></i>
              回复
            </el-button>
          </div>
        </div>
        <div class="reply-content">
          <div
            class="content-text"
            v-hljs
            v-html="marked(reply.content)"
          ></div>
        </div>
      </div>
    </transition-group>

    <!-- 回复输入框 -->
    <transition name="reply-form">
      <div v-show="commentShow" class="reply-form">
        <div class="reply-form-header">
          <el-avatar :src="attachImageUrl(currentUserAvatar)" :size="32" class="reply-avatar" />
          <div class="reply-input-container">
            <el-input
              v-model="reply.content"
              type="textarea"
              :placeholder="placeholder"
              :autosize="{ minRows: 2, maxRows: 4 }"
              class="reply-input"
              @keyup.enter.native="handleReplySubmit"
            />
            <div class="reply-actions">
              <el-button
                size="small"
                type="primary"
                @click="pushComment()"
                :loading="submitting"
                :disabled="!reply.content.trim() || submitting"
              >
                {{ submitting ? '发送中...' : '发送' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { pushComment, likeComment } from '@/api'
import { mixin } from "@/mixins"
import { marked } from 'marked'
import hljs from 'highlight.js'

export default {
  name: "CommentItem",
  mixins: [mixin],
  props: {
    articleId: {
      type: [String, Number],
      required: true
    },
    comment: {
      type: Object,
      required: true
    },
    index: {
      type: Number,
      required: true
    },
    rootCommentCounts: {
      type: Number,
      required: true
    },
    articleAuthorId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      placeholder: '你的评论...',
      commentShow: false,
      replyCommId: '',
      reply: this.getEmptyReply(),
      extension: false,
      marked: marked,
      likedComments: [],
      submitting: false
    }
  },
  computed: {
    isLiked() {
      return this.likedComments.includes(this.comment.id)
    },
    currentUserAvatar() {
      return this.$store.getters.avatar || '/avatarImages/default_user.jpg'
    }
  },
  mounted() {
    this.initMarkdownConfig()
    this.likedComments = this.$store.getters.likedComments
    console.log(this.likedComments)
  },
  methods: {
    initMarkdownConfig() {
      const renderer = new marked.Renderer()
      marked.setOptions({
        renderer: renderer,
        highlight: (code, lang) => {
          const language = hljs.getLanguage(lang) ? lang : 'plaintext'
          return hljs.highlight(code, { language }).value
        },
        pedantic: false,
        gfm: true,
        tables: true,
        breaks: false,
        sanitize: false,
        smartLists: true,
        smartypants: false,
        xhtml: false
      })
    },

    showComment(replyCommId, toUser) {
      this.reply = this.getEmptyReply()

      if (this.replyCommId !== replyCommId) {
        if (toUser) {
          this.placeholder = `@${toUser.username} `
          this.reply.toUser = toUser
        } else {
          this.placeholder = '你的评论...'
        }

        this.commentShow = true
        this.replyCommId = replyCommId
      } else {
        this.commentShow = false
        this.replyCommId = ''
      }
    },

    async pushComment() {
      if (!this.validateComment()) return

      this.submitting = true

      try {
        const params = new URLSearchParams()
        params.append("content", this.reply.content)
        params.append("articleId", this.reply.article.id)
        params.append("parentId", this.reply.parent.id)

        if (this.reply.toUser) {
          params.append("toUid", this.reply.toUser.id)
          params.append("level", "2")
        } else {
          params.append("level", "1")
        }

        const response = await pushComment(params)

        this.$message.success('评论成功')

        if (!this.comment.children) {
          this.comment.children = []
        }

        this.comment.children.unshift(response.data)
        this.$emit('commentCountsIncrement')
        this.extension = true
        this.showComment(this.replyCommId)
      } catch (error) {
        this.handleCommentError(error)
      } finally {
        this.submitting = false
      }
    },

    validateComment() {
      if (!this.$store.getters.loginIn) {
        this.$message.error('请先登录')
        return false
      }

      if (!this.reply.content.trim()) {
        this.$message.error('内容不能为空')
        return false
      }

      return true
    },

    handleCommentError(error) {
      if (error.status === 401) {
        this.$message.error('请重新登录')
        this.$store.commit('setLoginIn', false)
      } else {
        this.$message.error('评论失败')
      }
    },

    getEmptyReply() {
      return {
        article: {
          id: this.articleId
        },
        parent: {
          id: this.comment.id
        },
        toUser: null,
        content: ''
      }
    },

    toggleExtension() {
      this.extension = !this.extension
    },

    async toggleLike(commentId) {
      if (!this.$store.getters.loginIn) {
        this.$message.error('请先登录')
        return
      }

      try {
        const params = new URLSearchParams()
        params.append('commentId', commentId)
        params.append('type', 'comment')
        const response = await likeComment(params)

        if (response.data === 1) {
          this.likedComments.push(commentId)
          this.comment.likeCount = (this.comment.likeCount || 0) + 1
          this.$message.success('点赞成功')
        } else if (response.data === -1) {
          this.likedComments = this.likedComments.filter(id => id !== this.comment.id)
          this.comment.likeCount = Math.max(0, (this.comment.likeCount || 0) - 1)
          this.$message.success('取消点赞成功')
        }
        this.$store.commit('setLikedComments', this.likedComments)
      } catch (error) {
        console.error(error)
        this.$message.error('操作失败')
      }
    },
    async toggleReplyLike(replyId) {
      if (!this.$store.getters.loginIn) {
        this.$message.error('请先登录')
        return
      }
      try {
        const params = new URLSearchParams()
        params.append('commentId', replyId)
        params.append('type', 'reply')
        const response = await likeComment(params)

        if (response.data === 1) {
          this.likedComments.push(replyId)
          const reply = this.comment.children.find(r => r.id === replyId)
          if (reply) {
            reply.likeCount = (reply.likeCount || 0) + 1
          }
        } else if (response.data === -1) {
          this.likedComments = this.likedComments.filter(id => id !== replyId)
          const reply = this.comment.children.find(r => r.id === replyId)
          if (reply) {
            reply.likeCount = Math.max(0, (reply.likeCount || 0) - 1)
          }
        } else {
          this.$message.error(response.msg)
        }
      } catch (error) {
        console.error(error)
        this.$message.error('操作失败')
      }
    },
    isReplyLiked(replyId) {
      return this.likedComments.includes(replyId)
    },
    formatTime(timeString) {
      const date = new Date(timeString)
      const now = new Date()
      const diffMs = now - date
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

      if (diffDays === 0) {
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else if (diffDays < 7) {
        return `${diffDays}天前`
      } else {
        return date.toLocaleDateString('zh-CN')
      }
    },

    handleReplySubmit(event) {
      if (event.ctrlKey || event.metaKey) {
        this.pushComment()
      }
    }
  }
}
</script>

<style scoped>
.comment-item {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
  border: 1px solid #eef1f5;
}

.comment-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-1px);
}

.comment-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f2f5;
}

.comment-avatar {
  margin-right: 12px;
  border: 1px solid #e0e0e0;
  transition: all 0.2s ease;
}

.comment-avatar:hover {
  transform: scale(1.05);
  border-color: #409eff;
}

.comment-info {
  flex: 1;
}

.comment-author {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.author-name {
  font-weight: 600;
  color: #303133;
  margin-right: 8px;
  font-size: 1rem;
}

.author-tag {
  height: 18px;
  padding: 0 6px;
  font-size: 10px;
  line-height: 18px;
  border-radius: 9px;
  background-color: #f5c04f;
  color: #fff;
}

.comment-meta {
  display: flex;
  gap: 12px;
  color: #909399;
  font-size: 0.85rem;
}

.comment-time {
  color: #909399;
}

.comment-floor {
  color: #c0c4cc;
  background-color: #f4f4f5;
  padding: 1px 6px;
  border-radius: 8px;
  font-size: 0.75rem;
}

.comment-actions {
  display: flex;
  gap: 8px;
  margin-left: 12px;
}

.action-btn {
  color: #909399;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.2s ease;
  font-size: 0.85rem;
}

.action-btn:hover {
  color: #409eff;
  background-color: #f0f9ff;
}

.liked {
  color: #f56c6c !important;
  background-color: #fef0f0 !important;
}

.comment-content {
  margin-bottom: 12px;
  padding-left: 48px;
  color: #606266;
  line-height: 1.6;
  font-size: 0.95rem;
}

.comment-toolbar {
  padding-left: 48px;
  margin-bottom: 12px;
}

.toolbar-btn {
  color: #606266;
  padding: 4px 8px;
  border-radius: 6px;
  background-color: #f5f7fa;
  transition: all 0.2s ease;
  font-size: 0.85rem;
}

.toolbar-btn:hover {
  color: #409eff;
  background-color: #e6f0ff;
}

.replies-container {
  padding-left: 48px;
  border-left: 2px solid #409eff;
  margin-top: 12px;
  background-color: #fafafa;
  border-radius: 8px;
  padding: 8px;
}

.reply-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  border: 1px solid #eef1f5;
  transition: all 0.2s ease;
}

.reply-item:hover {
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
  transform: translateY(-1px);
}

.reply-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 6px;
  padding-bottom: 4px;
  border-bottom: 1px solid #f0f2f5;
}

.reply-avatar {
  margin-right: 8px;
  border: 1px solid #e0e0e0;
}

.reply-info {
  flex: 1;
}

.reply-author {
  display: flex;
  align-items: center;
  margin-bottom: 2px;
}

.reply-to {
  color: #78b6f7;
  margin-left: 8px;
  font-size: 0.85rem;
  background-color: #f0f9ff;
  padding: 1px 6px;
  border-radius: 8px;
}

.reply-meta {
  color: #909399;
  font-size: 0.8rem;
}

.reply-content {
  padding-left: 36px;
  color: #606266;
  line-height: 1.5;
  font-size: 0.9rem;
}

.reply-form {
  padding-left: 48px;
  margin-top: 16px;
  background-color: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #eef1f5;
}

.reply-form-header {
  display: flex;
  gap: 12px;
}

.reply-input-container {
  flex: 1;
}

.reply-input {
  margin-bottom: 8px;
}

.reply-actions {
  text-align: right;
}

/* 代码块样式 */
.content-text pre {
  background: #2d2d2d;
  border-radius: 6px;
  padding: 12px;
  overflow-x: auto;
  margin: 10px 0;
  color: #f8f8f2;
  font-size: 0.85rem;
  border: 1px solid #444;
}

.content-text code {
  font-family: 'Courier New', Consolas, Monaco, monospace;
  background-color: #f8f8f8;
  padding: 1px 4px;
  border-radius: 3px;
  font-size: 0.9em;
}

.content-text pre code {
  background: none;
  padding: 0;
  color: #f8f8f2;
  border: none;
}

/* 动画效果 */
.reply-list-enter-active, .reply-list-leave-active {
  transition: all 0.3s ease;
}
.reply-list-enter, .reply-list-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.reply-form-enter-active, .reply-form-leave-active {
  transition: all 0.3s ease;
}
.reply-form-enter, .reply-form-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .comment-header {
    flex-direction: column;
    gap: 8px;
  }

  .comment-actions {
    margin-left: 0;
    margin-top: 6px;
    justify-content: flex-end;
  }

  .comment-content,
  .comment-toolbar,
  .replies-container,
  .reply-form {
    padding-left: 0;
  }

  .reply-item {
    padding: 10px;
  }

  .reply-header {
    flex-direction: column;
    gap: 6px;
  }

  .reply-content {
    padding-left: 0;
  }

  .replies-container {
    padding-left: 8px;
    border-left: 1px solid #409eff;
  }
}
</style>
