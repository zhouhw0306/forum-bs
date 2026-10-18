<template>
  <section class="personal-card" aria-labelledby="following-profile-title" :aria-busy="String(loading)">
    <header class="personal-card-header">
      <h2 id="following-profile-title"><i class="el-icon-user"></i> 个人信息</h2>
      <router-link to="/center">我的主页 <i class="el-icon-top-right"></i></router-link>
    </header>
    <div class="personal-content">
      <div class="personal-overview">
        <div class="personal-identity">
          <router-link to="/center" class="personal-avatar-link" aria-label="查看我的个人主页"><img :src="avatarUrl" :alt="username || '我的头像'" @error="onAvatarError" /></router-link>
          <div class="personal-name"><h3>{{ username }}</h3><span class="personal-role">{{ role === 'ADMIN' ? '社区管理员' : '社区成员' }}</span></div>
        </div>
        <p v-if="loading" class="personal-bio">正在读取个人资料…</p>
        <p v-else-if="!profileError" class="personal-bio" :class="{ 'is-empty': !profile.introduction }">{{ profile.introduction || '还没有填写个人简介。' }}</p>
        <p v-if="profile.location" class="personal-location"><i class="el-icon-location-outline"></i>{{ profile.location }}</p>
      </div>
      <dl class="personal-stats" aria-label="社区数据">
        <div v-for="stat in stats" :key="stat.key" class="personal-stat"><dt>{{ stat.label }}</dt><dd><span v-if="loading" class="personal-stat-loading" aria-label="正在加载"></span><template v-else>{{ formatCount(personal[stat.key]) }}</template></dd></div>
      </dl>
      <div v-if="statsError || profileError" class="personal-error" role="status"><i class="el-icon-warning-outline"></i><span>{{ statsError ? '社区数据暂时无法加载' : '个人资料暂时无法加载' }}</span><button @click="init">重试</button></div>
      <div class="personal-actions"><router-link to="/write" class="personal-write"><i class="el-icon-edit-outline"></i> 发起讨论</router-link><router-link to="/setting" class="personal-edit"><i class="el-icon-setting"></i> 编辑个人资料</router-link></div>
    </div>
  </section>
</template>

<script>
import { mapGetters } from 'vuex'
import { mixin } from '@/mixins'
import { getAuthor, getPersonal } from '@/api'

export default {
  name: 'CardMe',
  mixins: [mixin],
  data: () => ({ personal: {}, profile: {}, loading: true, statsError: false, profileError: false, requestVersion: 0 }),
  computed: {
    ...mapGetters(['avatar', 'username', 'role']),
    avatarUrl() { return this.avatar ? this.attachImageUrl(this.avatar) : this.avatarFallback(this.username, 112) },
    stats() { return [{ key: 'idol', label: '关注' }, { key: 'followers', label: '粉丝' }, { key: 'writeNum', label: '讨论' }, { key: 'score', label: '积分' }] }
  },
  mounted() { this.init() },
  beforeDestroy() { this.requestVersion += 1 },
  methods: {
    onAvatarError(event) { event.target.onerror = null; event.target.src = this.avatarFallback(this.username, 112) },
    formatCount(value) { if (value === null || value === undefined || value === '') return '—'; const number = Number(value); return Number.isFinite(number) ? number.toLocaleString('zh-CN') : '—' },
    async init() {
      const version = ++this.requestVersion
      this.loading = true; this.statsError = false; this.profileError = false
      const [stats, author] = await Promise.allSettled([getPersonal(), getAuthor()])
      if (version !== this.requestVersion) return
      if (stats.status === 'fulfilled' && stats.value.code === 0 && stats.value.data) this.personal = stats.value.data
      else { this.personal = {}; this.statsError = true }
      if (author.status === 'fulfilled' && author.value.code === 0 && author.value.data) this.profile = { introduction: author.value.data.introduction, location: author.value.data.location }
      else { this.profile = {}; this.profileError = true }
      this.loading = false
    }
  }
}
</script>

<style scoped>
.personal-card { min-width: 0; background: var(--surface); border: 1px solid var(--border); border-radius: 6px; overflow: hidden; }
.personal-card-header { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 15px 20px; border-bottom: 1px solid var(--border-subtle); }
.personal-card-header h2 { display: flex; align-items: center; gap: 7px; margin: 0; color: var(--text); font-size: 13px; font-weight: 600; }
.personal-card-header h2 i { color: var(--text-muted); font-size: 16px; }
.personal-card-header > a { color: var(--text-muted); font-size: 11px; white-space: nowrap; }
.personal-card-header > a:hover { color: var(--accent); }
.personal-content { padding: 20px; }
.personal-identity { display: flex; align-items: center; gap: 14px; }
.personal-avatar-link { display: block; width: 56px; height: 56px; flex-shrink: 0; border-radius: 50%; }
.personal-avatar-link img { display: block; width: 100%; height: 100%; border: 1px solid var(--border-subtle); border-radius: 50%; object-fit: cover; }
.personal-name { min-width: 0; }
.personal-name h3 { margin: 0 0 5px; font-size: 18px; font-weight: 600; line-height: 1.4; overflow-wrap: anywhere; }
.personal-role { display: inline-block; padding: 2px 7px; border: 1px solid var(--border-subtle); border-radius: 12px; color: var(--text-muted); background: var(--surface-muted); font-size: 10px; }
.personal-bio { margin: 16px 0 0; color: var(--text); font-size: 12px; line-height: 1.8; white-space: pre-wrap; overflow-wrap: anywhere; }
.personal-bio.is-empty { color: var(--text-muted); }
.personal-location { display: flex; align-items: baseline; gap: 6px; margin: 10px 0 0; color: var(--text-muted); font-size: 11px; overflow-wrap: anywhere; }
.personal-location i { flex-shrink: 0; font-size: 14px; }
.personal-stats { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); margin: 20px 0 0; border: 1px solid var(--border-subtle); border-radius: 6px; background: var(--surface-muted); overflow: hidden; }
.personal-stat { padding: 12px 14px; min-width: 0; }
.personal-stat:nth-child(odd) { border-right: 1px solid var(--border-subtle); }
.personal-stat:nth-child(-n+2) { border-bottom: 1px solid var(--border-subtle); }
.personal-stat dt { color: var(--text-muted); font-size: 11px; }
.personal-stat dd { margin: 3px 0 0; min-height: 26px; color: var(--text); font-size: 20px; font-weight: 600; line-height: 1.3; font-variant-numeric: tabular-nums; overflow-wrap: anywhere; }
.personal-stat-loading { display: inline-block; width: 40px; height: 18px; margin-top: 4px; border-radius: 4px; background: var(--hover); }
.personal-error { display: flex; flex-wrap: wrap; align-items: center; gap: 6px; margin-top: 14px; color: var(--text-muted); font-size: 11px; }
.personal-error button { padding: 0; border: 0; background: transparent; color: var(--accent); font-size: 11px; cursor: pointer; }
.personal-actions { display: flex; flex-direction: column; gap: 8px; margin-top: 20px; }
.personal-actions a { display: flex; align-items: center; justify-content: center; gap: 6px; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 12px; font-weight: 500; line-height: 1.5; }
.personal-actions .personal-write { color: #fff; background: var(--success); border-color: #1b1f2426; }
.personal-write:hover { background: #1a7f37; }
.personal-actions .personal-edit { color: var(--text); background: var(--surface-muted); }
.personal-edit:hover { background: var(--hover); border-color: #afb8c1; }
@media (min-width: 601px) and (max-width: 900px) {
  .personal-content { display: grid; grid-template-columns: minmax(0, 1fr) minmax(220px, .9fr); gap: 16px 24px; }
  .personal-stats { margin-top: 0; align-self: start; }
  .personal-actions, .personal-error { grid-column: 1 / -1; margin-top: 0; }
  .personal-actions { flex-direction: row; } .personal-actions a { flex: 1; }
}
@media (max-width: 600px) {
  .personal-card-header { padding: 13px 16px; }
  .personal-content { padding: 16px; }
  .personal-stats { grid-template-columns: repeat(4, minmax(0, 1fr)); margin-top: 16px; }
  .personal-stat { padding: 10px 4px; text-align: center; }
  .personal-stat:nth-child(odd), .personal-stat:nth-child(-n+2) { border: 0; }
  .personal-stat + .personal-stat { border-left: 1px solid var(--border-subtle); }
  .personal-stat dd { font-size: 18px; min-height: 24px; }
  .personal-actions { flex-direction: row; margin-top: 16px; }
  .personal-actions a { flex: 1; min-width: 0; padding: 8px; }
}
</style>
