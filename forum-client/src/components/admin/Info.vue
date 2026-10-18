<template>
  <section class="dashboard">
    <div v-if="failed" class="dashboard-warning" role="status"><i class="el-icon-warning-outline"></i> 部分统计暂时无法加载。<button @click="loadAll">重新加载</button></div>
    <div class="dashboard-metrics"><article v-for="metric in metrics" :key="metric.label" class="dashboard-metric"><div><span>{{ metric.label }}</span><i :class="metric.icon"></i></div><el-skeleton v-if="loading" :rows="1" /><strong v-else>{{ metric.value === null ? '—' : metric.value.toLocaleString() }}</strong><p>{{ metric.hint }}</p></article></div>
    <div class="dashboard-grid">
      <section class="dashboard-panel growth-panel"><header><div><h2>成员增长趋势</h2><p>近 30 天的新增注册成员</p></div><span class="dashboard-period">最近 30 天</span></header><el-skeleton v-if="loading" :rows="5" animated class="dashboard-skeleton" /><ve-line v-else-if="userCount !== null" :data="registerTrend" :extend="lineExtend" :legend-visible="false" height="260px" /><div v-else class="dashboard-empty">暂无可用统计</div></section>
      <section class="dashboard-panel contributors-panel"><header><div><h2>社区贡献者</h2><p>按社区积分排序</p></div><i class="el-icon-trophy"></i></header><el-skeleton v-if="loading" :rows="5" animated class="dashboard-skeleton" /><ol v-else-if="topContributors.length" class="dashboard-contributors"><li v-for="(member, index) in topContributors" :key="member.id"><span class="contributor-order">{{ index + 1 }}</span><img :src="attachImageUrl(member.avatar)" :alt="member.username" @error="e => e.target.src = avatarFallback(member.username)" /><span class="contributor-identity"><strong>{{ member.username }}</strong><span>{{ member.role === 'ADMIN' ? '社区管理员' : '社区成员' }}</span></span><span class="dashboard-score">{{ member.score || 0 }}<small>积分</small></span></li></ol><div v-else class="dashboard-empty">暂无成员数据</div></section>
      <section class="dashboard-panel"><header><div><h2>成员地区分布</h2><p>注册资料中的地区信息</p></div><i class="el-icon-location-outline"></i></header><el-skeleton v-if="loading" :rows="4" animated class="dashboard-skeleton" /><ve-histogram v-else-if="userCount" :data="userArea" :extend="barExtend" :legend-visible="false" height="260px" /><div v-else class="dashboard-empty">暂无地区数据</div></section>
      <section class="dashboard-panel"><header><div><h2>社区角色分布</h2><p>成员与管理员构成</p></div><i class="el-icon-user"></i></header><el-skeleton v-if="loading" :rows="4" animated class="dashboard-skeleton" /><ve-ring v-else-if="userCount" :data="roleData" :settings="ringSettings" :extend="ringExtend" height="260px" /><div v-else class="dashboard-empty">暂无角色数据</div></section>
    </div>
    <div class="dashboard-footer"><span><i class="el-icon-data-line"></i> 人均积分 <strong>{{ avgScore === null ? '—' : avgScore }}</strong><span class="footer-divider"></span> 最高积分 <strong>{{ topScore === null ? '—' : topScore }}</strong></span><span>{{ updatedAt ? '最近更新 ' + updatedAt : '正在读取社区数据…' }}</span></div>
  </section>
</template>
<script>
import { mixin } from '@/mixins'
import { getAllUser, getSourceAll, getAllNoPage } from '@/api'
function dateKey(value) { const date = new Date(value); if (Number.isNaN(date.getTime())) return ''; return date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0') }
export default {
  mixins: [mixin],
  data() {
    return {
      loading: true, failed: false, updatedAt: '', users: [], userCount: null, newMembers: null, articleCount: null, sourceCount: null, avgScore: null, topScore: null,
      registerTrend: { columns: ['日期', '新增成员'], rows: [] }, userArea: { columns: ['地区', '成员'], rows: [] }, roleData: { columns: ['角色', '人数'], rows: [] },
      lineExtend: { color: ['#0969da'], grid: { top: 28, right: 24, bottom: 24, left: 20, containLabel: true }, xAxis: { axisLine: { lineStyle: { color: '#d0d7de' } }, axisTick: { show: false }, axisLabel: { color: '#656d76', fontSize: 10 } }, yAxis: { minInterval: 1, axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#656d76', fontSize: 10 }, splitLine: { lineStyle: { color: '#eaeef2', type: 'dashed' } } } },
      barExtend: { color: ['#54aeff'], grid: { top: 20, right: 20, bottom: 16, left: 20, containLabel: true }, xAxis: { axisLine: { lineStyle: { color: '#d0d7de' } }, axisTick: { show: false }, axisLabel: { color: '#656d76', fontSize: 10, interval: 0, rotate: 20 } }, yAxis: { minInterval: 1, axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#656d76', fontSize: 10 }, splitLine: { lineStyle: { color: '#eaeef2', type: 'dashed' } } } },
      ringSettings: { radius: [50, 72], offsetY: 106 }, ringExtend: { color: ['#0969da', '#afb8c1'], legend: { bottom: 8, top: 'auto', textStyle: { color: '#656d76', fontSize: 11 } } }
    }
  },
  computed: {
    metrics() { return [
      { label: '社区成员', value: this.userCount, icon: 'el-icon-user', hint: '已注册的社区成员' },
      { label: '讨论总数', value: this.articleCount, icon: 'el-icon-chat-dot-round', hint: '在分享中积累知识' },
      { label: '已上线资源', value: this.sourceCount, icon: 'el-icon-folder-opened', hint: '通过审核的共享资源' },
      { label: '昨日新成员', value: this.newMembers, icon: 'el-icon-circle-plus-outline', hint: '昨天加入社区的开发者' }
    ] },
    topContributors() { return this.users.slice().sort((a, b) => (b.score || 0) - (a.score || 0)).slice(0, 5) }
  },
  mounted() { this.loadAll() },
  methods: {
    async loadAll() {
      this.loading = true; this.failed = false
      const results = await Promise.allSettled([getAllUser(), getAllNoPage(), getSourceAll('1')])
      results.forEach((result, index) => {
        if (result.status !== 'fulfilled' || result.value.code !== 0) { this.failed = true; return }
        const data = result.value.data
        if (index === 0 && Array.isArray(data)) this.buildUsers(data)
        else if (index === 1 && typeof data === 'number') this.articleCount = data
        else if (index === 2 && Array.isArray(data)) this.sourceCount = data.length
        else this.failed = true
      })
      this.updatedAt = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }); this.loading = false
    },
    buildUsers(users) {
      this.users = users; this.userCount = users.length
      const yesterday = new Date(); yesterday.setDate(yesterday.getDate() - 1)
      this.newMembers = users.filter(user => user.createTime && dateKey(user.createTime) === dateKey(yesterday)).length
      const dates = {}; for (let i = 29; i >= 0; i--) { const date = new Date(); date.setDate(date.getDate() - i); dates[dateKey(date)] = 0 }
      const areas = {}; const roles = {}
      users.forEach(user => { const key = user.createTime ? dateKey(user.createTime) : ''; if (Object.prototype.hasOwnProperty.call(dates, key)) dates[key] += 1; const area = (user.location || '未填写').replace(/省|市|自治区|壮族|维吾尔|回族|特别行政区/g, '').trim() || '未填写'; areas[area] = (areas[area] || 0) + 1; const role = user.role === 'ADMIN' ? '管理员' : '社区成员'; roles[role] = (roles[role] || 0) + 1 })
      this.registerTrend = { columns: ['日期', '新增成员'], rows: Object.entries(dates).map(([date, count]) => ({ 日期: date.slice(5), 新增成员: count })) }
      this.userArea = { columns: ['地区', '成员'], rows: Object.entries(areas).sort((a, b) => b[1] - a[1]).slice(0, 8).map(([area, count]) => ({ 地区: area, 成员: count })) }
      this.roleData = { columns: ['角色', '人数'], rows: Object.entries(roles).map(([role, count]) => ({ 角色: role, 人数: count })) }
      const scores = users.map(user => Number(user.score) || 0); this.avgScore = scores.length ? Math.round(scores.reduce((sum, score) => sum + score, 0) / scores.length) : 0; this.topScore = scores.length ? Math.max(...scores) : 0
    }
  }
}
</script>
<style scoped>
.dashboard-metrics { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; margin-bottom: 20px; }
.dashboard-metric { min-width: 0; padding: 18px 20px; background: var(--surface); border: 1px solid var(--border); border-radius: 6px; }
.dashboard-metric > div { display: flex; align-items: center; justify-content: space-between; gap: 8px; color: var(--text-muted); font-size: 11px; } .dashboard-metric > div i { font-size: 18px; } .dashboard-metric > strong { display: block; font-size: 30px; font-weight: 600; letter-spacing: -1px; line-height: 1.3; margin: 14px 0 10px; font-variant-numeric: tabular-nums; } .dashboard-metric > p { color: var(--text-muted); font-size: 10px; line-height: 1.7; }
.dashboard-metric .el-skeleton { margin: 16px 0; }
.dashboard-grid { display: grid; grid-template-columns: minmax(0, 1.45fr) minmax(0, 1fr); gap: 20px; }
.dashboard-panel { background: var(--surface); border: 1px solid var(--border); border-radius: 6px; min-width: 0; overflow: hidden; }
.dashboard-panel > header { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; padding: 20px 20px 0; } .dashboard-panel h2 { font-size: 13px; font-weight: 600; line-height: 1.5; } .dashboard-panel header p { color: var(--text-muted); font-size: 10px; line-height: 1.7; margin-top: 4px; } .dashboard-panel header > i { color: var(--text-muted); font-size: 17px; }
.dashboard-period { padding: 3px 8px; background: var(--surface-muted); border: 1px solid var(--border-subtle); border-radius: 4px; color: var(--text-muted); font-size: 10px; white-space: nowrap; }
.dashboard-contributors { list-style: none; padding: 12px 20px 8px; } .dashboard-contributors li { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid var(--border-subtle); } .dashboard-contributors li:last-child { border: 0; }
.contributor-order { min-width: 10px; font-size: 10px; color: var(--text-muted); } .dashboard-contributors img { width: 28px; height: 28px; object-fit: cover; border-radius: 50%; border: 1px solid var(--border-subtle); flex-shrink: 0; }
.contributor-identity { display: flex; flex-direction: column; gap: 2px; min-width: 0; } .contributor-identity strong { color: var(--text); font-size: 11px; font-weight: 500; text-overflow: ellipsis; overflow: hidden; white-space: nowrap; } .contributor-identity > span { color: var(--text-muted); font-size: 9px; }
.dashboard-score { display: flex; flex-direction: column; align-items: flex-end; margin-left: auto; color: var(--text); font-size: 12px; font-weight: 500; font-variant-numeric: tabular-nums; } .dashboard-score small { color: var(--text-muted); font-size: 9px; font-weight: 400; }
.dashboard-footer { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-top: 20px; padding: 16px 0; border-top: 1px solid var(--border); color: var(--text-muted); font-size: 10px; } .dashboard-footer > span:first-child { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; } .dashboard-footer strong { color: var(--text); font-weight: 600; } .footer-divider { height: 12px; width: 1px; background: var(--border); margin: 0 8px; }
.dashboard-empty { display: grid; place-items: center; min-height: 240px; color: var(--text-muted); font-size: 12px; } .dashboard-skeleton { padding: 28px 20px; min-height: 260px; }
.dashboard-warning { display: flex; flex-wrap: wrap; align-items: center; gap: 8px; padding: 12px 16px; margin-bottom: 16px; border: 1px solid #d4a72c66; background: #fff8c5; border-radius: 6px; color: #7d4e00; font-size: 12px; } .dashboard-warning button { padding: 0; border: 0; background: none; color: var(--accent); font-size: 12px; cursor: pointer; }
@media (max-width: 1150px) { .dashboard-metrics { grid-template-columns: repeat(2, minmax(0, 1fr)); } .dashboard-metric { padding: 16px 18px; } }
@media (max-width: 700px) { .dashboard-grid { grid-template-columns: minmax(0, 1fr); gap: 16px; } .dashboard-metrics { gap: 12px; } .dashboard-metric { padding: 14px; } .dashboard-metric > strong { font-size: 27px; margin: 10px 0 8px; } .dashboard-metric > p { font-size: 9px; } .dashboard-footer { flex-wrap: wrap; gap: 12px; } }
</style>
