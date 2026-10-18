<template>
  <aside class="admin-navigation" :class="{ 'is-collapsed': collapsed }" aria-label="管理导航">
    <div class="admin-navigation-label"><span>工作空间</span><span class="admin-label-badge">ADMIN</span></div>
    <nav><router-link v-for="item in items" :key="item.path" :to="item.path" :class="['admin-nav-item', { 'nav-separated': item.separated }]" :title="item.title"><i :class="item.icon"></i><span>{{ item.title }}</span><i class="el-icon-arrow-right nav-arrow"></i></router-link></nav>
    <button class="admin-nav-collapse" @click="$emit('toggle')" :aria-label="collapsed ? '展开管理导航' : '收起管理导航'"><i :class="collapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i><span>{{ collapsed ? '' : '收起导航' }}</span></button>
    <div class="admin-nav-note" v-if="!collapsed"><i class="el-icon-chat-dot-square"></i><strong>让社区更好一点。</strong><p>每一次维护，都是对分享者的支持。</p><router-link to="/">回到社区 <i class="el-icon-right"></i></router-link></div>
  </aside>
</template>
<script>
export default {
  props: { collapsed: Boolean },
  data: () => ({ items: [
    { path: '/info', title: '社区概览', icon: 'el-icon-data-analysis' },
    { path: '/articleManage', title: '讨论管理', icon: 'el-icon-document', separated: true },
    { path: '/commentManage', title: '评论管理', icon: 'el-icon-chat-dot-round' },
    { path: '/sourceManage', title: '资源管理', icon: 'el-icon-folder-opened' },
    { path: '/userManage', title: '成员管理', icon: 'el-icon-user', separated: true }
  ] })
}
</script>
<style scoped>
.admin-navigation { position: sticky; top: 88px; min-width: 0; }
.admin-navigation-label { display: flex; align-items: center; justify-content: space-between; gap: 6px; font-size: 11px; color: var(--text-muted); padding: 0 10px; margin-bottom: 14px; }
.admin-label-badge { padding: 1px 5px; border: 1px solid var(--border); border-radius: 4px; font-size: 8px; letter-spacing: .4px; }
.admin-nav-item { display: flex; align-items: center; gap: 10px; position: relative; min-height: 40px; padding: 10px; margin-bottom: 4px; border-radius: 6px; color: var(--text); font-size: 12px; line-height: 1.5; }
.admin-nav-item > i:first-child { color: var(--text-muted); font-size: 17px; flex-shrink: 0; } .admin-nav-item:hover { background: var(--hover); color: var(--text); }
.admin-nav-item.router-link-active { background: var(--hover); font-weight: 600; } .admin-nav-item.router-link-active::before { content: ''; position: absolute; left: -5px; top: 11px; bottom: 11px; width: 3px; border-radius: 3px; background: #fd8c73; }
.nav-arrow { margin-left: auto; font-size: 10px; color: #8c959f; opacity: 0; } .router-link-active .nav-arrow { opacity: 1; } .nav-separated { margin-top: 14px; }
.admin-nav-collapse { display: flex; align-items: center; gap: 10px; width: 100%; padding: 10px; border: 0; border-top: 1px solid var(--border); background: transparent; font-size: 11px; color: var(--text-muted); cursor: pointer; margin-top: 20px; } .admin-nav-collapse > i { font-size: 17px; } .admin-nav-collapse:hover { color: var(--text); }
.admin-nav-note { padding: 24px 10px; margin-top: 16px; border-top: 1px solid var(--border); font-size: 11px; color: var(--text-muted); line-height: 1.8; } .admin-nav-note > i { display: block; font-size: 22px; margin-bottom: 12px; } .admin-nav-note strong { color: var(--text); font-size: 12px; font-weight: 500; } .admin-nav-note p { margin: 6px 0 12px; }
.is-collapsed .admin-navigation-label { visibility: hidden; } .is-collapsed .admin-nav-item { justify-content: center; } .is-collapsed .admin-nav-item > span, .is-collapsed .nav-arrow { display: none; } .is-collapsed .admin-nav-collapse { justify-content: center; }
@media (max-width: 900px) { .admin-navigation { position: static; overflow-x: auto; padding-bottom: 6px; border-bottom: 1px solid var(--border); } .admin-navigation-label, .admin-nav-collapse, .admin-nav-note { display: none; } .admin-navigation nav { display: flex; gap: 4px; min-width: max-content; } .admin-nav-item, .nav-separated { margin: 0; padding: 9px 12px; min-height: 36px; gap: 6px; } .admin-nav-item > i:first-child { font-size: 15px; } .is-collapsed .admin-nav-item > span { display: inline; } .nav-arrow, .admin-nav-item.router-link-active::before { display: none; } }
</style>
