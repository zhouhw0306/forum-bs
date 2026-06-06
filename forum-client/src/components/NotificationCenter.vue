<template>
  <div class="nc-dropdown">
    <div class="nc-head">
      <span class="nc-head-title">通知</span>
      <el-button type="text" size="mini" @click="handleReadAll">全部已读</el-button>
      <el-button type="text" size="mini" @click="goToAll">查看全部</el-button>
    </div>
    <div class="nc-body" v-loading="loading">
      <div v-if="notifications.length === 0" class="nc-empty">
        <i class="el-icon-bell"></i>
        <p>暂无通知</p>
      </div>
      <div
        v-for="item in notifications"
        :key="item.id"
        :class="['nc-item', { unread: item.isRead === 0 }]"
        @click="handleClick(item)"
      >
        <div class="nc-item-dot" v-if="item.isRead === 0"></div>
        <div class="nc-item-body">
          <div class="nc-item-head">
            <span class="nc-item-type">{{ getTypeLabel(item.type) }}</span>
            <span class="nc-item-title">{{ item.title }}</span>
          </div>
          <p class="nc-item-text">{{ item.content }}</p>
          <span class="nc-item-time">{{ formatTime(item.createTime) }}</span>
        </div>
        <el-button
          class="nc-item-del"
          type="text"
          size="mini"
          icon="el-icon-close"
          @click.stop="handleDelete(item)"
        ></el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getNotifyList, readNotify, readAllNotify, deleteNotify } from "@/api";

export default {
  name: "NotificationCenter",
  data() {
    return {
      notifications: [],
      loading: false
    };
  },
  mounted() {
    this.fetchNotifications();
  },
  methods: {
    async fetchNotifications() {
      this.loading = true;
      try {
        const res = await getNotifyList({ pageNo: 1, pageSize: 8 });
        if (res.code === 0 && res.data) {
          this.notifications = res.data.records || [];
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
          this.$emit("read");
        } catch (e) {
          console.error("标记已读失败:", e);
        }
      }
      if (item.resourceType && item.resourceId) {
        this.goToResource(item);
      }
    },
    goToResource(item) {
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
          this.$emit("read-all");
          this.$message.success("已全部已读");
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
        this.$emit("delete");
      } catch (e) {
        console.error("删除通知失败:", e);
      }
    },
    goToAll() {
      this.$emit("read-all");
      this.$router.push("/notifications");
    },
    formatTime(time) {
      if (!time) return "";
      const d = new Date(time);
      const now = new Date();
      const diff = now - d;
      const min = Math.floor(diff / 60000);
      const hour = Math.floor(diff / 3600000);
      const day = Math.floor(diff / 86400000);
      if (min < 1) return "刚刚";
      if (min < 60) return `${min}分钟前`;
      if (hour < 24) return `${hour}小时前`;
      if (day < 7) return `${day}天前`;
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, "0");
      const dd = String(d.getDate()).padStart(2, "0");
      return `${y}-${m}-${dd}`;
    },
    getTypeLabel(type) {
      const map = {
        COMMENT: "评论", REPLY: "回复", LIKE: "点赞",
        FAVOUR: "收藏", FOLLOW: "关注", SYSTEM: "系统"
      };
      return map[type] || type;
    }
  }
};
</script>

<style scoped>
.nc-dropdown {
  max-height: 420px;
  display: flex;
  flex-direction: column;
}
.nc-head {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 10px 14px;
  border-bottom: 1px solid #f0f0f0;
}
.nc-head-title {
  flex: 1;
  font-weight: 600;
  font-size: 14px;
  color: #1a1a2e;
}
.nc-body {
  flex: 1;
  overflow-y: auto;
}
.nc-empty {
  text-align: center;
  padding: 36px 0;
  color: #c0c4cc;
}
.nc-empty i { font-size: 32px; }
.nc-empty p { margin-top: 8px; font-size: 13px; }

.nc-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 14px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.15s;
}
.nc-item:hover { background: #fafbfc; }
.nc-item.unread { background: #f5f9ff; }

.nc-item-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #409eff;
  flex-shrink: 0;
  margin-top: 6px;
}
.nc-item-body { flex: 1; min-width: 0; }
.nc-item-head {
  display: flex;
  gap: 6px;
  align-items: baseline;
  margin-bottom: 3px;
}
.nc-item-type {
  font-size: 11px;
  color: #909399;
  flex-shrink: 0;
}
.nc-item-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.nc-item-text {
  margin: 0 0 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.nc-item-time { font-size: 11px; color: #c0c4cc; }
.nc-item-del {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.15s;
  color: #c0c4cc;
}
.nc-item:hover .nc-item-del { opacity: 1; }
.nc-item-del:hover { color: #f56c6c; }
</style>
