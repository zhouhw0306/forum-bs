<template>
  <el-card class="community-card" shadow="never"><div slot="header" class="community-card-title"><i class="el-icon-trophy"></i><span>社区贡献者</span><span class="card-caption">积分榜</span></div>
    <el-skeleton v-if="loading" :rows="3" animated />
    <div v-else-if="failed" class="sidebar-state">暂时无法加载 <button @click="init">重试</button></div>
    <ol v-else-if="users.length" class="contributor-list"><li v-for="(user, index) in users.slice(0, 5)" :key="user.id"><span class="contributor-rank">{{ index + 1 }}</span><img :src="attachImageUrl(user.avatar)" :alt="user.username" @error="e => e.target.src = avatarFallback(user.username)" /><span class="contributor-name" :title="user.introduction">{{ user.username }}</span><span class="contributor-score">{{ user.score }}</span></li></ol>
    <p v-else class="sidebar-state">分享知识，成为社区贡献者。</p>
  </el-card>
</template>
<script>
import { getByScore } from '@/api'
import { mixin } from '@/mixins'
export default {
  name: 'CardList', mixins: [mixin], data: () => ({ users: [], loading: true, failed: false }),
  mounted() { this.init() },
  methods: { async init() { this.loading = true; this.failed = false; try { const res = await getByScore(); if (res.code !== 0) throw new Error(res.msg); this.users = res.data || [] } catch (error) { this.failed = true } finally { this.loading = false } } }
}
</script>
<style scoped>
.card-caption { color: var(--text-muted); font-size: 11px; font-weight: 400; margin-left: auto; }
.contributor-list { margin: 0; padding: 0; list-style: none; }
.contributor-list li { display: flex; align-items: center; gap: 9px; padding: 8px 0; } .contributor-list li:first-child { padding-top: 0; } .contributor-list li:last-child { padding-bottom: 0; }
.contributor-rank { color: var(--text-muted); font-size: 11px; width: 10px; flex-shrink: 0; }
.contributor-list img { width: 26px; height: 26px; border-radius: 50%; border: 1px solid var(--border-subtle); object-fit: cover; flex-shrink: 0; }
.contributor-name { color: var(--text); font-size: 12px; flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.contributor-score { color: var(--text-muted); font-size: 11px; font-variant-numeric: tabular-nums; }
</style>
