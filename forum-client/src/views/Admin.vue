<template>
  <div class="admin-page">
    <div class="admin-heading"><div><div class="page-eyebrow"><router-link to="/">CodeBase</router-link><i class="el-icon-arrow-right"></i><span>管理中心</span></div><h1>{{ currentPage.title }}</h1><p>{{ currentPage.description }}</p></div><div class="admin-heading-actions"><el-button size="small" icon="el-icon-refresh" @click="revision += 1">刷新数据</el-button><router-link to="/" class="admin-community-link">返回社区 <i class="el-icon-top-right"></i></router-link></div></div>
    <div class="admin-layout" :class="{ 'is-collapsed': collapse }"><the-aside :collapsed="collapse" @toggle="collapse = !collapse" /><main class="admin-content"><router-view :key="$route.path + ':' + revision" /></main></div>
  </div>
</template>
<script>
import TheAside from '@/components/admin/TheAside'
const pages = {
  '/info': { title: '社区概览', description: '从每一次分享与交流，了解社区的成长。' },
  '/userManage': { title: '成员管理', description: '查看社区成员，管理账号状态与访问权限。' },
  '/articleManage': { title: '讨论管理', description: '发现有价值的内容，维护友善、有序的讨论空间。' },
  '/commentManage': { title: '评论管理', description: '查看评论与回复，让每一次交流都更有价值。' },
  '/sourceManage': { title: '资源管理', description: '审核与整理共享资源，让优质内容被更多人发现。' }
}
export default {
  components: { TheAside }, data: () => ({ collapse: false, revision: 0 }),
  computed: { currentPage() { return pages[this.$route.path] || pages['/info'] } }
}
</script>
<style scoped>
.admin-page { max-width: 1440px; padding: 28px 32px 48px; margin: 0 auto; }
.admin-heading { display: flex; align-items: center; justify-content: space-between; gap: 24px; padding-bottom: 28px; margin-bottom: 28px; border-bottom: 1px solid var(--border); }
.admin-heading .page-eyebrow { font-size: 11px; } .page-eyebrow a { color: var(--text-muted); } .page-eyebrow i { font-size: 10px; }
.admin-heading h1 { font-size: 26px; font-weight: 600; letter-spacing: -.6px; margin-bottom: 8px; line-height: 1.4; } .admin-heading p { color: var(--text-muted); font-size: 13px; }
.admin-heading-actions { display: flex; align-items: center; gap: 20px; flex-shrink: 0; } .admin-community-link { color: var(--text-muted); font-size: 12px; } .admin-community-link:hover { color: var(--accent); }
.admin-layout { display: grid; grid-template-columns: 188px minmax(0, 1fr); align-items: start; gap: 28px; } .admin-layout.is-collapsed { grid-template-columns: 52px minmax(0, 1fr); }
.admin-content { min-width: 0; }
@media (max-width: 1050px) { .admin-layout { grid-template-columns: 164px minmax(0, 1fr); gap: 20px; } .admin-community-link { display: none; } }
@media (max-width: 900px) { .admin-page { padding: 24px; } .admin-layout, .admin-layout.is-collapsed { display: block; } .admin-content { margin-top: 20px; } }
@media (max-width: 600px) { .admin-page { padding: 24px 16px 32px; } .admin-heading { align-items: flex-start; gap: 10px; padding-bottom: 20px; margin-bottom: 20px; } .admin-heading h1 { font-size: 23px; } .admin-heading p { font-size: 12px; line-height: 1.7; } .admin-heading-actions { margin-top: 24px; } .admin-heading-actions .el-button { padding: 9px; font-size: 11px; } }
</style>
<style lang="scss">
@use '../assets/css/admin.scss';
</style>
