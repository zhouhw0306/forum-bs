<template>
  <nav class="category-navigation" aria-label="讨论分类">
    <div class="navigation-label">探索社区</div>
    <router-link :to="{ path: '/', query: {} }" :class="['category-link', { active: !selected }]" @click.native="$emit('selected')"><i class="el-icon-chat-dot-round"></i> 全部讨论</router-link>
    <router-link :to="{ path: '/', query: { sw: 'care' } }" :class="['category-link', { active: selected === 'care' }]" @click.native="$emit('selected')"><i class="el-icon-star-off"></i> 关注动态</router-link>
    <div class="navigation-label category-label">技术分类</div>
    <router-link v-for="(cat, index) in categories" :key="cat.id" :to="{ path: '/', query: { sw: String(cat.id) } }" :class="['category-link', { active: selected === String(cat.id) }]" @click.native="$emit('selected')"><i :class="catIcon(index)"></i><span>{{ cat.categoryName }}</span></router-link>
    <p v-if="failed" class="category-feedback">分类暂时无法加载 <button @click="fetchCategories">重试</button></p>
    <div class="navigation-note"><i class="el-icon-edit-outline"></i><strong>每个想法，都值得交流。</strong><p>记录你的探索，分享你的发现。</p><router-link to="/write">写下第一篇讨论 <i class="el-icon-right"></i></router-link></div>
  </nav>
</template>
<script>
import { getAllCategorys } from '@/api'
const icons = ['el-icon-cpu', 'el-icon-monitor', 'el-icon-mobile-phone', 'el-icon-magic-stick', 'el-icon-coin', 'el-icon-set-up', 'el-icon-connection', 'el-icon-folder-opened', 'el-icon-suitcase', 'el-icon-question']
export default {
  data: () => ({ categories: [], failed: false }),
  computed: { selected() { return this.$route.query.sw || '' } },
  mounted() { this.fetchCategories() },
  methods: {
    catIcon(index) { return icons[index % icons.length] },
    async fetchCategories() {
      this.failed = false
      try { const res = await getAllCategorys(); if (res.code !== 0) throw new Error(res.msg); this.categories = res.data || [] } catch (error) { this.failed = true }
    }
  }
}
</script>
<style scoped>
.navigation-label { padding: 0 12px; margin: 0 0 10px; font-size: 12px; color: var(--text-muted); font-weight: 600; }
.category-label { padding-top: 20px; margin-top: 16px; border-top: 1px solid var(--border); }
.category-link { display: flex; align-items: center; gap: 10px; min-height: 38px; padding: 8px 12px; margin: 3px 0; border-radius: 6px; color: var(--text); font-size: 13px; line-height: 1.5; position: relative; }
.category-link i { font-size: 17px; color: var(--text-muted); flex-shrink: 0; }
.category-link:hover { color: var(--text); background: var(--hover); }
.category-link.active { background: #eaeef2; font-weight: 600; }
.category-link.active::before { position: absolute; left: -5px; top: 8px; bottom: 8px; content: ''; width: 3px; background: #fd8c73; border-radius: 3px; }
.navigation-note { padding: 20px 12px; margin-top: 20px; border-top: 1px solid var(--border); font-size: 12px; color: var(--text-muted); line-height: 1.8; }
.navigation-note > i { display: block; margin-bottom: 10px; font-size: 22px; color: var(--text-muted); }
.navigation-note strong { color: var(--text); font-weight: 500; } .navigation-note p { margin: 5px 0 10px; }
.category-feedback { padding: 12px; color: var(--text-muted); font-size: 12px; } .category-feedback button { color: var(--accent); background: none; border: 0; cursor: pointer; }
</style>
