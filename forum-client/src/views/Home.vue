<template>
  <div id="home" class="app-shell">
    <the-header :activeIndex="$route.path" />
    <router-view class="me-container" />
    <footer class="site-footer"><span class="footer-brand"><i class="el-icon-chat-dot-square"></i> CodeBase <span>© {{ new Date().getFullYear() }}</span></span><span>连接开发者，让知识流动。</span><div><router-link to="/source">资源广场</router-link><router-link to="/searchPage">探索社区</router-link></div></footer>
  </div>
</template>

<script>
import TheHeader from "../components/TheHeader";

export default {
  name: 'Home',
  components: {TheHeader},
  data() {
    return {
      activeIndex: '/'
    }
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.activeIndex = to.path
    })
  }
}
</script>

<style scoped>

.app-shell { min-height: 100vh; padding-top: 64px; display: flex; flex-direction: column; }
.me-container { flex: 1; min-width: 0; width: 100%; }
.site-footer { display: flex; align-items: center; justify-content: center; gap: 28px; border-top: 1px solid var(--border); padding: 24px; margin-top: 24px; color: var(--text-muted); font-size: 12px; }
.footer-brand { display: flex; align-items: center; gap: 7px; color: var(--text); font-weight: 600; } .footer-brand span { margin-left: 5px; color: var(--text-muted); font-weight: 400; }
.site-footer > div { display: flex; gap: 18px; } .site-footer a { color: var(--text-muted); } .site-footer a:hover { color: var(--accent); }
@media (max-width: 600px) { .site-footer { flex-wrap: wrap; gap: 12px 24px; padding: 24px 16px; } .site-footer > span:nth-child(2) { display: none; } }

</style>
