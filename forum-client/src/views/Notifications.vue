<template>
  <div class="notify-page">
    <div class="notify-container">
      <!-- 顶部栏 -->
      <div class="notify-topbar">
        <div class="notify-topbar-left">
          <h2 class="notify-page-title">通知中心</h2>
          <span v-if="unreadCount > 0" class="notify-unread-badge">{{ unreadCount }} 条未读</span>
        </div>
        <el-button
          type="primary"
          plain
          size="medium"
          icon="el-icon-check"
          :disabled="unreadCount === 0"
          @click="handleReadAll"
        >
          全部已读
        </el-button>
      </div>

      <!-- 筛选 Tab -->
      <div class="notify-tabs">
        <button
          :class="['notify-tab', { active: activeTab === 'all' }]"
          @click="switchTab('all')"
        >全部</button>
        <button
          :class="['notify-tab', { active: activeTab === 'unread' }]"
          @click="switchTab('unread')"
        >未读</button>
      </div>

      <!-- 列表区域 -->
      <div v-loading="loading" class="notify-body" element-loading-text="加载中...">
        <!-- 空状态 -->
        <div v-if="!loading && notifications.length === 0" class="notify-empty">
          <div class="empty-icon-wrap">
            <i class="el-icon-bell"></i>
          </div>
          <p class="empty-text">{{ activeTab === 'unread' ? '没有未读通知' : '暂无通知' }}</p>
          <p class="empty-sub">当有人与你互动时，通知会显示在这里</p>
        </div>

        <!-- 通知列表 -->
        <div v-else class="notify-list">
          <div
            v-for="item in notifications"
            :key="item.id"
            :class="['notify-card', { unread: item.isRead === 0 }]"
            @click="handleClick(item)"
          >
            <!-- 类型图标 -->
            <div :class="['notify-card-icon', iconClass(item.type)]">
              <i :class="iconName(item.type)"></i>
            </div>
            <!-- 内容 -->
            <div class="notify-card-body">
              <div class="notify-card-head">
                <span class="notify-card-title">{{ item.title }}</span>
                <span v-if="item.isRead === 0" class="notify-dot"></span>
              </div>
              <p class="notify-card-text">{{ item.content }}</p>
              <span class="notify-card-time">{{ formatTime(item.createTime) }}</span>
            </div>
            <!-- 操作 -->
            <el-button
              class="notify-card-del"
              type="text"
              icon="el-icon-close"
              size="small"
              @click.stop="handleDelete(item)"
            ></el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="notify-pager">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange"
          hide-on-single-page
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getNotifyList, readNotify, readAllNotify, deleteNotify } from "@/api";

export default {
  name: "Notifications",
  data() {
    return {
      notifications: [],
      loading: false,
      activeTab: "all",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      unreadCount: 0
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    switchTab(tab) {
      if (this.activeTab === tab) return;
      this.activeTab = tab;
      this.currentPage = 1;
      this.fetchData();
    },
    async fetchData() {
      this.loading = true;
      try {
        const res = await getNotifyList({ pageNo: this.currentPage, pageSize: this.pageSize });
        if (res.code === 0 && res.data) {
          const records = res.data.records || [];
          this.total = res.data.total || 0;
          // 后端没有按 is_read 筛选，前端过滤
          this.notifications = this.activeTab === "unread"
            ? records.filter(item => item.isRead === 0)
            : records;
          this.unreadCount = records.filter(item => item.isRead === 0).length;
        }
      } catch (e) {
        console.error("获取通知列表失败:", e);
      } finally {
        this.loading = false;
      }
    },
    async handleClick(item) {
      if (item.isRead === 0) {
        try {
          await readNotify(item.id);
          item.isRead = 1;
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        } catch (e) {
          console.error("标记已读失败:", e);
        }
      }
      this.navigateToResource(item);
    },
    navigateToResource(item) {
      if (!item.resourceType || !item.resourceId) return;
      const map = {
        ARTICLE: `/view/${item.resourceId}`,
        COMMENT: `/view/${item.resourceId}`,
        SOURCE: `/details/${item.resourceId}`
      };
      const path = map[item.resourceType];
      if (path) this.$router.push(path);
    },
    async handleReadAll() {
      try {
        const res = await readAllNotify();
        if (res.code === 0) {
          this.notifications.forEach(n => (n.isRead = 1));
          this.unreadCount = 0;
          this.$message.success("已全部标记为已读");
        } else {
          this.$message.error(res.msg || "操作失败");
        }
      } catch (e) {
        this.$message.error("网络错误");
      }
    },
    async handleDelete(item) {
      try {
        await deleteNotify(item.id);
        this.notifications = this.notifications.filter(n => n.id !== item.id);
        this.total--;
        if (item.isRead === 0) this.unreadCount--;
        this.$message.success("已删除");
      } catch (e) {
        this.$message.error("删除失败");
      }
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchData();
    },
    // ---- 辅助 ----
    formatTime(time) {
      if (!time) return "";
      const d = new Date(time);
      const now = new Date();
      const diff = now - d;
      const min = Math.floor(diff / 60000);
      const hour = Math.floor(diff / 3600000);
      const day = Math.floor(diff / 86400000);
      if (min < 1) return "刚刚";
      if (min < 60) return `${min} 分钟前`;
      if (hour < 24) return `${hour} 小时前`;
      if (day < 7) return `${day} 天前`;
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, "0");
      const dd = String(d.getDate()).padStart(2, "0");
      return `${y}-${m}-${dd}`;
    },
    iconName(type) {
      const map = {
        COMMENT: "el-icon-chat-line-round",
        REPLY: "el-icon-chat-dot-round",
        LIKE: "el-icon-star-on",
        FAVOUR: "el-icon-star-off",
        FOLLOW: "el-icon-user-solid",
        SYSTEM: "el-icon-info"
      };
      return map[type] || "el-icon-bell";
    },
    iconClass(type) {
      const map = {
        COMMENT: "icon-comment",
        REPLY: "icon-reply",
        LIKE: "icon-like",
        FAVOUR: "icon-favour",
        FOLLOW: "icon-follow",
        SYSTEM: "icon-system"
      };
      return map[type] || "icon-default";
    }
  }
};
</script>

<style scoped>
/* ============ 基础 ============ */
.notify-page {
  width: 100%;
  min-height: calc(100vh - 60px);
  /* 背景透明，透出 canvas-nest 粒子动画 */
  box-sizing: border-box;
}
.notify-container {
  width: 90%;
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 0 48px;
}

/* ============ 顶部栏 ============ */
.notify-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.notify-topbar-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.notify-page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  letter-spacing: -0.5px;
}
.notify-unread-badge {
  font-size: 13px;
  color: #909399;
  background: #e9eaec;
  padding: 2px 10px;
  border-radius: 10px;
}

/* ============ Tab 切换 ============ */
.notify-tabs {
  display: flex;
  gap: 0;
  background: #fff;
  border-radius: 10px 10px 0 0;
  padding: 4px;
  margin-bottom: 0;
  border-bottom: 1px solid #eef0f4;
}
.notify-tab {
  flex: 1;
  padding: 10px 0;
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  color: #909399;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}
.notify-tab:hover {
  color: #409eff;
  background: #f0f5ff;
}
.notify-tab.active {
  color: #409eff;
  background: #ecf5ff;
  font-weight: 600;
}

/* ============ 列表 ============ */
.notify-body {
  background: #fff;
  border-radius: 0 0 10px 10px;
  min-height: 320px;
}
.notify-list {
  padding: 4px 0;
}

/* ---- 空状态 ---- */
.notify-empty {
  text-align: center;
  padding: 80px 20px;
}
.empty-icon-wrap {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.empty-icon-wrap i {
  font-size: 32px;
  color: #c0c4cc;
}
.empty-text {
  font-size: 15px;
  color: #606266;
  margin: 0 0 6px;
}
.empty-sub {
  font-size: 13px;
  color: #a8abb2;
  margin: 0;
}

/* ---- 单条通知 ---- */
.notify-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 14px 20px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #f5f6f8;
}
.notify-card:last-child {
  border-bottom: none;
}
.notify-card:hover {
  background: #fafbfc;
}
.notify-card.unread {
  background: #f5f9ff;
}
.notify-card.unread:hover {
  background: #eef4fd;
}

/* 左侧图标 */
.notify-card-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  color: #fff;
}
/* 各类型颜色 */
.icon-comment  { background: #909399; }
.icon-reply    { background: #409eff; }
.icon-like     { background: #e6a23c; }
.icon-favour   { background: #e6a23c; }
.icon-follow   { background: #67c23a; }
.icon-system   { background: #f56c6c; }
.icon-default  { background: #909399; }

/* 中间内容 */
.notify-card-body {
  flex: 1;
  min-width: 0;
}
.notify-card-head {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}
.notify-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d2b;
}
.notify-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #409eff;
  flex-shrink: 0;
}
.notify-card-text {
  margin: 0 0 6px;
  font-size: 13px;
  color: #6b6f78;
  line-height: 1.55;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.notify-card-time {
  font-size: 12px;
  color: #b0b3bb;
}

/* 右侧删除 */
.notify-card-del {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.15s;
  color: #c0c4cc;
  margin-top: 2px;
}
.notify-card:hover .notify-card-del {
  opacity: 1;
}
.notify-card-del:hover {
  color: #f56c6c;
}

/* ============ 分页 ============ */
.notify-pager {
  display: flex;
  justify-content: center;
  padding: 20px 0 0;
}
</style>
