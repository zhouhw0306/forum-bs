<template>
  <div class="detail-page">
    <div class="detail-container">
      <!-- 返回 -->
      <div class="detail-back" @click="goBack">
        <i class="el-icon-arrow-left"></i> 返回
      </div>

      <!-- 内容卡 -->
      <div class="detail-card">
        <!-- 作者 -->
        <div class="detail-author">
          <img :src="attachImageUrl(source.user?.avatar)" class="detail-author-avatar" @error="e => e.target.src = avatarFallback(source.user?.username)" />
          <div class="detail-author-info">
            <span class="detail-author-name">{{ source.user?.username }}</span>
            <span class="detail-author-time">{{ formatTime(source.createTime) }}</span>
          </div>
        </div>

        <!-- 正文 -->
        <h1 class="detail-title">{{ source.title }}</h1>
        <p class="detail-desc" v-if="source.description">{{ source.description }}</p>
        <div class="detail-body" v-html="urlToLink(source.content)"></div>

        <!-- 下载 -->
        <div v-if="source.fileName" class="detail-download">
          <el-link icon="el-icon-download" :href="source.fileUrl" type="primary" :underline="false">
            {{ source.fileName }}
          </el-link>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="detail-actions">
        <button :class="['action-btn', { active: source.hasThumb }]" @click="thumb(source.id)">
          <i :class="source.hasThumb ? 'fa fa-thumbs-up' : 'fa fa-thumbs-o-up'"></i>
          <span>点赞 {{ source.thumbNum || 0 }}</span>
        </button>
        <button :class="['action-btn', { active: source.hasFavour }]" @click="favour(source.id)">
          <i :class="source.hasFavour ? 'fa fa-star' : 'fa fa-star-o'"></i>
          <span>收藏 {{ source.favourNum || 0 }}</span>
        </button>
        <button class="action-btn" @click="share">
          <i class="el-icon-share"></i>
          <span>分享</span>
        </button>
      </div>

      <!-- 评论区 -->
      <div class="comment-card">
        <h3 class="comment-title">{{ totalComments }} 条评论</h3>

        <!-- 发表评论 -->
        <div class="comment-write">
          <img :src="attachImageUrl(avatar)" class="comment-write-avatar" @error="e => e.target.src = avatarFallback(username)" />
          <div class="comment-write-body">
            <VueEmoji ref="emoji" width="100%" height="80" :value="comment.content" @input="onInput" />
            <div class="comment-write-foot">
              <span class="comment-hint">支持 Markdown 和 Emoji</span>
              <el-button type="primary" size="small" @click="pushComment" :disabled="!comment.content.trim()">发表评论</el-button>
            </div>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list" v-if="comments.length > 0">
          <commment-item
            v-for="(c, i) in comments"
            :key="c.id"
            :comment="c"
            :articleId="source.id"
            :articleAuthorId="source.user?.id"
            :index="i"
            :rootCommentCounts="comments.length"
          />
        </div>
        <el-empty v-else :image-size="80" description="暂无评论" />
      </div>
    </div>
  </div>
</template>

<script>
import VueEmoji from "emoji-vue2";
import { mixin } from "@/mixins";
import { favour, getCommentsByArticle, getSourceById, pushComment, thumb } from "@/api";
import CommmentItem from "@/components/CommentItem";

export default {
  mixins: [mixin],
  components: { CommmentItem, VueEmoji },
  data() {
    return {
      id: "",
      source: { id: "", createTime: "", title: "", description: "", content: "", hasThumb: "", thumbNum: "", hasFavour: "", favourNum: "", user: { avatar: "", username: "" } },
      comments: [],
      comment: { content: "" },
    };
  },
  mounted() {
    window.scrollTo({ top: 0, left: 0 });
    this.id = this.$route.params.id;
    this.init();
  },
  computed: {
    avatar() {
      return this.$store.getters.avatar || "/avatarImages/default_user.jpg";
    },
    totalComments() {
      let sum = this.comments.length;
      this.comments.forEach((c) => { if (c.childrens) sum += c.childrens.length; });
      return sum;
    },
  },
  methods: {
    onInput(event) { this.comment.content = event.data; },
    init() {
      getSourceById(this.id).then((res) => {
        this.source = res.data;
        this.getCommentsByArticle();
      }).catch(() => {});
    },
    thumb(targetId) {
      let p = new URLSearchParams(); p.append("targetId", targetId);
      // 乐观更新：先切换本地状态
      const prev = this.source.hasThumb;
      this.source.hasThumb = !prev;
      this.source.thumbNum = (this.source.thumbNum || 0) + (prev ? -1 : 1);
      thumb(p).then((res) => {
        if (res.data !== 1 && res.data !== -1) {
          // 失败时回滚
          this.source.hasThumb = prev;
          this.source.thumbNum = (this.source.thumbNum || 0) + (prev ? 1 : -1);
          this.$message.error(res.msg || "操作失败");
        }
      }).catch(() => {
        this.source.hasThumb = prev;
        this.source.thumbNum = (this.source.thumbNum || 0) + (prev ? 1 : -1);
      });
    },
    favour(targetId) {
      let p = new URLSearchParams(); p.append("targetId", targetId);
      const prev = this.source.hasFavour;
      this.source.hasFavour = !prev;
      this.source.favourNum = (this.source.favourNum || 0) + (prev ? -1 : 1);
      favour(p).then((res) => {
        if (res.data !== 1 && res.data !== -1) {
          this.source.hasFavour = prev;
          this.source.favourNum = (this.source.favourNum || 0) + (prev ? 1 : -1);
          this.$message.error(res.msg || "操作失败");
        }
      }).catch(() => {
        this.source.hasFavour = prev;
        this.source.favourNum = (this.source.favourNum || 0) + (prev ? 1 : -1);
      });
    },
    pushComment() {
      if (!this.comment.content.trim()) return this.$message.error("评论不能为空");
      if (!this.$store.getters.loginIn) return this.$message.error("请先登录");
      let p = new URLSearchParams();
      p.append("content", this.comment.content);
      p.append("articleId", this.source.id);
      p.append("level", "0");
      pushComment(p).then((res) => {
        if (res.code === 0) {
          this.$message.success("评论成功");
          this.comments.unshift(res.data);
          this.$refs.emoji.clear();
          this.comment.content = "";
        } else {
          this.$message.error(res.msg);
        }
      }).catch((err) => {
        if (err.status === 401) {
          this.$store.commit("setLoginIn", false);
          this.$message.error("请重新登录");
        }
      });
    },
    getCommentsByArticle() {
      let p = new URLSearchParams(); p.append("articleId", this.source.id);
      getCommentsByArticle(p).then((res) => { this.comments = res.data; }).catch(() => {});
    },
    share() {
      const url = `http://localhost:8080${this.$route.fullPath}`;
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(url).then(() => this.$message.success("链接已复制")).catch(() => {});
      } else {
        this.$message.info(url);
      }
    },
    goBack() { this.$router.go(-1); },
    formatTime(t) {
      if (!t) return "";
      const d = new Date(t);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
    },
  },
};
</script>

<style scoped>
.detail-page { min-height: calc(100vh - 60px); background: transparent; }
.detail-container { max-width: 800px; width: 92%; margin: 0 auto; padding: 24px 0 60px; }

/* 返回 */
.detail-back {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 14px; color: #606266; cursor: pointer;
  padding: 8px 0; margin-bottom: 12px; transition: color .2s;
}
.detail-back:hover { color: #409eff; }

/* 内容卡 */
.detail-card {
  background: #fff; border-radius: 16px; padding: 28px 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05); margin-bottom: 14px;
}
.detail-author { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.detail-author-avatar { width: 44px; height: 44px; border-radius: 50%; object-fit: cover; }
.detail-author-name { display: block; font-size: 15px; font-weight: 600; color: #1a1a2e; }
.detail-author-time { display: block; font-size: 12px; color: #909399; margin-top: 2px; }
.detail-title { margin: 0 0 14px; font-size: 26px; font-weight: 700; color: #1a1a2e; line-height: 1.4; }
.detail-desc { margin: 0 0 20px; padding: 14px 16px; background: #f7f8fa; border-radius: 10px; font-size: 14px; color: #606266; line-height: 1.6; border-left: 3px solid #409eff; }
.detail-body { font-size: 15px; color: #303133; line-height: 1.8; word-break: break-word; }
.detail-body >>> a { color: #409eff; }
.detail-body >>> img { max-width: 100%; border-radius: 8px; }
.detail-download {
  margin-top: 24px; padding-top: 18px; border-top: 1px solid #f0f0f0;
}

/* 操作栏 */
.detail-actions {
  display: flex; gap: 0;
  background: #fff; border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05); margin-bottom: 14px;
  overflow: hidden;
}
.action-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 14px 0; border: none; background: transparent;
  font-size: 14px; color: #606266; cursor: pointer;
  transition: background .15s, color .15s;
}
.action-btn:not(:last-child) { border-right: 1px solid #f0f0f0; }
.action-btn:hover { background: #fafbfc; color: #409eff; }
.action-btn.active { color: #409eff; }
.action-btn.active i { color: #409eff; }

/* 评论区 */
.comment-card {
  background: #fff; border-radius: 16px; padding: 28px 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);
}
.comment-title { margin: 0 0 20px; font-size: 16px; font-weight: 600; color: #1a1a2e; }

.comment-write { display: flex; gap: 14px; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #f0f0f0; }
.comment-write-avatar { width: 38px; height: 38px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }
.comment-write-body { flex: 1; min-width: 0; }
.comment-write-foot { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.comment-hint { font-size: 12px; color: #c0c4cc; }

.comment-list { margin-top: 8px; }
</style>
