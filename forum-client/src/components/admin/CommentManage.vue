<template>
  <div class="cm-page">
    <!-- 顶部栏 -->
    <div class="cm-topbar">
      <div class="cm-topbar-left">
        <el-input v-model="keyword" size="small" placeholder="搜索评论内容或用户名..." prefix-icon="el-icon-search" clearable class="cm-search" />
        <el-select v-model="filterLevel" size="small" placeholder="层级筛选" clearable class="cm-filter">
          <el-option label="全部" value="" />
          <el-option label="主评论" value="0" />
          <el-option label="回复" value="2" />
        </el-select>
      </div>
      <span class="cm-count">{{ total }} 条评论</span>
    </div>

    <!-- 评论列表 -->
    <div v-loading="loading" class="cm-list">
      <div v-if="pagedData.length === 0" class="cm-empty">
        <i class="el-icon-chat-line-round"></i> 暂无评论
      </div>
      <div v-for="item in pagedData" :key="item.id" class="cm-card" :class="{ 'cm-is-reply': item.level !== '0' }">
        <!-- 主评论头部 -->
        <div class="cm-card-top">
          <img v-if="item.user" :src="attachImageUrl(item.user.avatar)" class="cm-card-avatar" @error="e => e.target.src = avatarFallback(item.user.username)" />
          <div class="cm-card-meta">
            <span class="cm-card-user">{{ item.user?.username || '匿名' }}</span>
            <span class="cm-card-time">{{ item.createTime }}</span>
            <el-tag v-if="item.level !== '0'" size="mini" type="info" effect="plain" class="cm-tag">回复</el-tag>
          </div>
          <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="handleDelete(item)">删除</el-button>
        </div>
        <!-- 评论内容 -->
        <p class="cm-card-text">{{ item.content }}</p>
        <!-- 所属文章 -->
        <div class="cm-card-foot">
          <i class="el-icon-document"></i> 帖子: {{ item.articleId }}
          <span class="cm-card-likes"><i class="el-icon-star-off"></i> {{ item.likeCount || 0 }}</span>
        </div>
        <!-- 子评论 -->
        <div v-if="item.children && item.children.length" class="cm-replies">
          <div class="cm-reply-head" @click="toggleChildren(item.id)">
            <i :class="openIds.includes(item.id) ? 'el-icon-arrow-down' : 'el-icon-arrow-right'"></i>
            {{ item.children.length }} 条回复
          </div>
          <div v-if="openIds.includes(item.id)" class="cm-reply-list">
            <div v-for="child in item.children" :key="child.id" class="cm-card cm-reply-card">
              <div class="cm-card-top">
                <img v-if="child.user" :src="attachImageUrl(child.user.avatar)" class="cm-card-avatar" @error="e => e.target.src = avatarFallback(child.user.username)" />
                <div class="cm-card-meta">
                  <span class="cm-card-user">{{ child.user?.username || '匿名' }}</span>
                  <span v-if="child.toUser" class="cm-reply-to">回复 @{{ child.toUser?.username }}</span>
                  <span class="cm-card-time">{{ child.createTime }}</span>
                </div>
                <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="handleDelete(child)">删除</el-button>
              </div>
              <p class="cm-card-text">{{ child.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination v-if="total > pageSize" class="cm-pager" background layout="prev, pager, next"
      :total="total" :page-size="pageSize" :current-page.sync="currentPage" />
  </div>
</template>

<script>
import { deleteComment, getCommentAll } from "@/api";
import { mixin } from "@/mixins";

export default {
  mixins: [mixin],
  data() {
    return {
      allData: [], keyword: "", filterLevel: "",
      currentPage: 1, pageSize: 15,
      openIds: [], loading: false,
    };
  },
  computed: {
    filteredData() {
      let list = this.allData;
      if (this.keyword) {
        const kw = this.keyword.toLowerCase();
        list = list.filter(r =>
          (r.content || "").toLowerCase().includes(kw) ||
          (r.user?.username || "").toLowerCase().includes(kw) ||
          (r.articleId || "").toLowerCase().includes(kw)
        );
      }
      if (this.filterLevel) {
        list = list.filter(r => r.level === this.filterLevel);
      }
      return list;
    },
    pagedData() {
      const s = (this.currentPage - 1) * this.pageSize;
      return this.filteredData.slice(s, s + this.pageSize);
    },
    total() { return this.filteredData.length; },
  },
  watch: {
    keyword() { this.currentPage = 1; },
    filterLevel() { this.currentPage = 1; },
  },
  mounted() { this.loadData(); },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const res = await getCommentAll();
        if (res.code === 0) this.allData = res.data || [];
      } catch (e) { this.$message.error("加载失败"); }
      finally { this.loading = false; }
    },
    toggleChildren(id) {
      const i = this.openIds.indexOf(id);
      i > -1 ? this.openIds.splice(i, 1) : this.openIds.push(id);
    },
    handleDelete(row) {
      this.$confirm(`确认删除「${(row.user?.username || '匿名')}」的评论？删除后不可恢复。`, "删除评论", {
        confirmButtonText: "确认删除",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        const p = new URLSearchParams(); p.append("id", row.id); p.append("level", row.level);
        deleteComment(p).then(res => {
          if (res.code === 0) {
            this.$message.success("已删除");
            this.allData = this.allData.filter(r => r.id !== row.id);
          } else {
            this.$message.error(res.msg);
          }
        }).catch(() => this.$message.error("删除失败"));
      }).catch(() => {});
    },
  },
};
</script>

<style scoped>
.cm-page { padding: 0; }

/* 顶部 */
.cm-topbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; gap: 12px; }
.cm-topbar-left { display: flex; gap: 12px; }
.cm-search { width: 260px; }
.cm-filter { width: 120px; }
.cm-count { font-size: 13px; color: #909399; white-space: nowrap; }

/* 列表 */
.cm-list { min-height: 200px; }
.cm-empty { text-align: center; padding: 60px 0; color: #c0c4cc; font-size: 14px; }

/* 卡片 */
.cm-card {
  background: #fff; border-radius: 10px;
  padding: 16px 20px; margin-bottom: 10px;
  box-shadow: 0 1px 6px rgba(0,0,0,.04);
  border-left: 4px solid #409eff;
  transition: box-shadow .15s;
}
.cm-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,.08); }
.cm-is-reply { border-left-color: #909399; }
.cm-card-top { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.cm-card-avatar { width: 30px; height: 30px; border-radius: 50%; object-fit: cover; }
.cm-card-meta { flex: 1; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.cm-card-user { font-weight: 600; font-size: 14px; color: #1a1a2e; }
.cm-card-time { font-size: 12px; color: #c0c4cc; }
.cm-tag { margin-left: 4px; }
.cm-card-text { margin: 0 0 8px; font-size: 14px; color: #303133; line-height: 1.65; word-break: break-word; }
.cm-card-foot { font-size: 12px; color: #909399; display: flex; gap: 16px; }
.cm-card-foot i { margin-right: 3px; }
.cm-card-likes { margin-left: auto; }

/* 回复区 */
.cm-replies { margin-top: 12px; padding-top: 10px; border-top: 1px dashed #ebeef5; }
.cm-reply-head { font-size: 13px; color: #409eff; cursor: pointer; user-select: none; padding: 4px 0; }
.cm-reply-head i { margin-right: 4px; font-size: 12px; }
.cm-reply-list { margin-top: 8px; }
.cm-reply-card { border-left-color: #e6a23c; background: #fafbfc; padding: 12px 16px; margin-bottom: 6px; }
.cm-reply-card:hover { box-shadow: none; }
.cm-reply-to { font-size: 12px; color: #909399; }
.cm-reply-to::before { content: '·'; margin: 0 4px; }

.cm-pager { margin-top: 20px; text-align: center; }
</style>
