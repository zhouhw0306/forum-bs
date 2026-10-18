<template>
  <el-card class="community-card" shadow="never"><div slot="header" class="community-card-title"><i class="el-icon-bell"></i><span>社区公告</span></div>
    <el-skeleton v-if="loading" :rows="2" animated />
    <div v-else-if="failed" class="sidebar-state">暂时无法加载 <button @click="init">重试</button></div>
    <ul v-else-if="notification.length" class="notice-list"><li v-for="notice in notification" :key="notice.id"><span class="notice-dot"></span><button @click="open(notice.title, notice.content)">{{ notice.title }}</button></li></ul>
    <p v-else class="sidebar-state">暂无新公告，欢迎参与社区讨论。</p>
  </el-card>
</template>
<script>
import { getAllNotify } from '@/api'
export default {
  name: 'CardMsg', data: () => ({ loading: true, failed: false, notification: [] }),
  mounted() { this.init() },
  methods: {
    async init() { this.loading = true; this.failed = false; try { const res = await getAllNotify(); if (res.code !== 0) throw new Error(res.msg); this.notification = res.data || [] } catch (error) { this.failed = true } finally { this.loading = false } },
    open(title, content) { this.$alert(content, title, { confirmButtonText: '我知道了' }) }
  }
}
</script>
<style scoped>
.notice-list { list-style: none; padding: 0; margin: 0; } .notice-list li { display: flex; align-items: baseline; gap: 9px; margin-bottom: 12px; } .notice-list li:last-child { margin-bottom: 0; }
.notice-dot { width: 5px; height: 5px; border-radius: 50%; background: var(--success); flex-shrink: 0; }
.notice-list button { border: 0; padding: 0; text-align: left; background: none; color: var(--text); font-size: 13px; cursor: pointer; line-height: 1.6; } .notice-list button:hover { color: var(--accent); }
</style>
