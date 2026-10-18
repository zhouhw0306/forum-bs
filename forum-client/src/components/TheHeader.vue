<template>
  <header class="site-header" :class="{ 'is-simple': simple }" @keydown.esc="mobileMenu = false">
    <div class="header-inner">
      <router-link to="/" class="brand" aria-label="CodeBase 首页">
        <span class="brand-mark"><svg width="23" height="23" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m8 7-5 5 5 5m8-10 5 5-5 5M14 4l-4 16"/></svg></span>
        <span>CodeBase</span>
      </router-link>
      <span class="header-divider"></span>
      <button v-if="!simple" class="icon-button mobile-menu-toggle" @click="toggleMobileMenu" :aria-expanded="String(mobileMenu)" aria-controls="community-navigation" aria-label="切换导航菜单"><i :class="mobileMenu ? 'el-icon-close' : 'el-icon-menu'"></i></button>
      <nav v-if="!simple" id="community-navigation" class="header-navigation" :class="{ 'is-open': mobileMenu }" aria-label="主导航">
        <router-link to="/" exact @click.native="mobileMenu = false"><i class="el-icon-chat-dot-square"></i>{{ $t('common.home') }}</router-link>
        <router-link to="/source" @click.native="mobileMenu = false"><i class="el-icon-collection"></i>{{ $t('common.resources') }}</router-link>
        <router-link to="/searchPage" @click.native="mobileMenu = false"><i class="el-icon-discover"></i>{{ $t('common.search') }}</router-link>
        <router-link v-if="loginIn" to="/carePost" @click.native="mobileMenu = false">{{ $t('common.following') }}</router-link>
        <router-link v-if="role === 'ADMIN'" to="/info" @click.native="mobileMenu = false">{{ $t('common.admin') }}</router-link>
        <form class="header-search" role="search" @submit.prevent="toSearch(); mobileMenu = false">
          <i class="el-icon-search" aria-hidden="true"></i>
          <input v-model="inputValue" aria-label="搜索社区" placeholder="搜索文章、资源、开发者…" />
          <button type="submit" aria-label="搜索"><i class="el-icon-right"></i></button>
        </form>
      </nav>
      <div v-else class="header-slot"><slot></slot></div>
      <div class="header-account">
        <button class="icon-button language-button" @click="switchLang" :aria-label="$i18n.locale === 'zh' ? '切换语言' : 'Switch language'" :title="$i18n.locale === 'zh' ? 'Switch to English' : '切换为中文'">{{ $i18n.locale === 'zh' ? '中' : 'EN' }}</button>
        <template v-if="!loginIn">
          <router-link to="/login" class="login-link">{{ $t('common.login') }}</router-link>
          <router-link to="/register" class="register-link">{{ $t('common.register') }}</router-link>
        </template>
        <template v-else>
          <el-tooltip content="DeepSeek AI 助手" placement="bottom"><button class="icon-button" aria-label="打开 AI 助手" @click="drawer = true"><i class="el-icon-magic-stick"></i></button></el-tooltip>
          <el-popover placement="bottom-end" width="340" trigger="click" v-model="notifyVisible" @show="onNotifyPanelOpen">
            <NotificationCenter ref="notifyCenter" @read="onNotifyRead" @read-all="onNotifyReadAll" @delete="onNotifyRead" />
            <button slot="reference" class="icon-button notification-button" aria-label="查看通知"><i class="el-icon-bell"></i><sup v-if="unreadCount > 0" class="notification-count">{{ unreadCount > 99 ? '99+' : unreadCount }}</sup></button>
          </el-popover>
          <el-popover :visible-arrow="false" placement="bottom-end" width="180" trigger="click">
            <div class="account-menu">
              <span class="account-name">{{ username }}</span>
              <el-button @click="setting"><i class="el-icon-user"></i> 个人中心</el-button>
              <el-button @click="$router.push('/setting')"><i class="el-icon-setting"></i> 账号设置</el-button>
              <el-button @click="write"><i class="el-icon-edit"></i> 发起讨论</el-button>
              <el-button class="logout-button" @click="logout"><i class="el-icon-switch-button"></i> 退出登录</el-button>
            </div>
            <button slot="reference" class="avatar-button" aria-label="打开个人菜单"><img :src="attachImageUrl(avatar)" :alt="username" @error="e => e.target.src = avatarFallback(username)" /><i class="el-icon-caret-bottom"></i></button>
          </el-popover>
          <el-drawer title="AI 编程助手" :with-header="false" :visible.sync="drawer" size="min(680px, 100vw)" direction="rtl" :append-to-body="true" custom-class="ai-assistant-drawer"><AiModel :active="drawer" @close="drawer = false" /></el-drawer>
        </template>
      </div>
    </div>
  </header>
</template>

<script>
import {mixin} from "@/mixins";
import { mapGetters } from 'vuex';
import AiModel from "@/components/aiModel/AiModel";
import NotificationCenter from "@/components/NotificationCenter";
import { getUnreadCount } from "@/api";

export default {
  name: 'TheHeader',
  components: {AiModel, NotificationCenter},
  mixins: [mixin],
  props: {
    activeIndex: String,
    simple: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      inputValue: "", //搜索框输入
      drawer: false,
      wsManager: null,
      unreadCount: 0,
      notifyVisible: false,
      mobileMenu: false
    }
  },
  computed: {
    ...mapGetters([
      'userId',
      'avatar',
      'username',
      'role',
      'loginIn'
    ]),
  },
  watch: {
    // 监听登录状态变化
    loginIn: {
      handler(newVal) {
        if (newVal && this.userId) {
          // 用户登录后初始化WebSocket
          this.initWebSocket();
          this.fetchUnreadCount();
        } else {
          // 用户登出时关闭WebSocket连接
          this.closeWebSocket();
          this.unreadCount = 0;
        }
      },
      immediate: true
    }
  },
  beforeDestroy() { this.closeWebSocket() },
  methods: {
    toggleMobileMenu() { this.mobileMenu = !this.mobileMenu; },
    switchLang() {
      const next = this.$i18n.locale === 'zh' ? 'en' : 'zh';
      this.$i18n.locale = next;
      localStorage.setItem('lang', next);
    },
    async fetchUnreadCount() {
      if (!this.loginIn) return
      try {
        const res = await getUnreadCount()
        if (res.code === 0) {
          this.unreadCount = res.data
        }
      } catch (e) {
        console.error('获取未读通知数失败', e)
      }
    },
    onNotifyPanelOpen() {
      this.fetchUnreadCount()
    },
    onNotifyRead() {
      this.fetchUnreadCount()
    },
    onNotifyReadAll() {
      this.unreadCount = 0
    },
    async initWebSocket() {
      console.log('WebSocket初始化ing');
      if (!this.wsManager && this.userId) {
        try {
          const websocketModule = await import('@/assets/js/websocket');
          const WebSocketManager = websocketModule.default;

          this.wsManager = new WebSocketManager(this.userId);
          this.wsManager.init();

          // 注册消息回调：收到通知时更新未读计数
          this.wsManager.onMessage((msg) => {
            this.fetchUnreadCount();
            // 弹出通知提示
            this.$notify({
              title: msg.title || '新通知',
              message: msg.content || '',
              type: msg.type === 'ERROR' ? 'error' : (msg.type || 'info').toLowerCase(),
              duration: 5000,
              position: 'top-right'
            });
          });
        } catch (error) {
          console.error('WebSocket模块加载失败:', error);
        }
      }
    },
    closeWebSocket() {
      if (this.wsManager) {
        this.wsManager.close();
        this.wsManager = null;
      }
    },
    write(){
      this.$router.push('/write')
    },
    toSearch(){
      this.$router.push({path: `/searchPage`,query : {inputValue : this.inputValue}})
    },
    toWrite(){
      if (!this.$store.getters.loginIn){
        this.$message({type: 'error', message: '请先登录', showClose: true})
        return;
      }
      this.$router.push({path: '/write'})
    },
    logout() {
      this.closeWebSocket(); // 关闭WebSocket连接

      this.$store.commit('setLoginIn', false) //是否登录
      this.$store.commit('setUserId', '')  //用户id
      this.$store.commit('setUsername', '')  //用户名
      this.$store.commit('setAvatar', '')  //头像url
      this.$store.commit('setRole', '')  //身份
      this.$store.commit('setToken','') //存储用户信息到浏览器
      this.$store.commit('setLikedComments',null)
      this.$router.push('/')
      this.$router.go(0)
    },
    setting() {
      if(this.loginIn){
        this.$router.push({path:'/center'})
      }
    }
  }
}
</script>

<style scoped>

.site-header { position: fixed; inset: 0 0 auto; height: 64px; z-index: 1100; border-bottom: 1px solid var(--border); background: var(--surface-muted); }
.header-inner { height: 100%; max-width: 1504px; margin: auto; padding: 0 32px; display: flex; align-items: center; gap: 24px; }
.brand { display: inline-flex; align-items: center; gap: 10px; color: var(--text); font-size: 20px; font-weight: 650; letter-spacing: -.6px; flex-shrink: 0; }
.brand:hover { color: var(--text); }
.brand-mark { width: 34px; height: 34px; display: grid; place-items: center; background: #1f2328; color: #fff; border-radius: 8px; }
.header-divider { width: 1px; height: 24px; background: var(--border); }
.header-navigation { display: flex; align-items: center; gap: 22px; flex: 1; min-width: 0; height: 100%; }
.header-navigation > a { position: relative; display: inline-flex; align-items: center; gap: 6px; color: var(--text-muted); font-size: 14px; white-space: nowrap; height: 100%; }
.header-navigation > a:hover, .header-navigation > a.router-link-active { color: var(--text); }
.header-navigation > a.router-link-active { font-weight: 600; }
.header-navigation > a.router-link-active::after { content: ''; position: absolute; bottom: -1px; left: 0; right: 0; height: 3px; background: #fd8c73; border-radius: 2px; }
.header-navigation > a i { font-size: 17px; }
.header-search { display: flex; align-items: center; gap: 8px; margin-left: auto; width: clamp(160px, 22vw, 300px); height: 34px; border: 1px solid var(--border); border-radius: 6px; background: var(--surface); padding: 0 10px; color: var(--text-muted); }
.header-search:focus-within { border-color: var(--accent); outline: 2px solid var(--focus); }
.header-search input { min-width: 0; flex: 1; width: 100%; border: 0; outline: none; background: transparent; font: inherit; font-size: 12px; color: var(--text); }
.header-search button { border: 0; background: none; color: var(--text-muted); cursor: pointer; padding: 2px; }
.header-account { display: flex; align-items: center; gap: 14px; margin-left: auto; flex-shrink: 0; font-size: 13px; }
.login-link { color: var(--text); white-space: nowrap; }
.register-link { padding: 5px 14px; color: var(--text); background: var(--surface); border: 1px solid var(--border); border-radius: 6px; font-weight: 500; white-space: nowrap; }
.register-link:hover { background: var(--hover); color: var(--text); }
.icon-button { display: inline-flex; align-items: center; justify-content: center; width: 32px; height: 32px; border: 1px solid transparent; border-radius: 6px; background: transparent; color: var(--text-muted); font-size: 19px; cursor: pointer; }
.icon-button:hover { background: var(--hover); color: var(--text); }
.language-button { font-size: 12px; font-weight: 600; }
.notification-button { position: relative; }
.notification-count { position: absolute; top: -4px; right: -5px; min-width: 16px; padding: 0 3px; border: 2px solid var(--surface-muted); border-radius: 10px; color: white; background: var(--accent); font-size: 9px; line-height: 14px; }
.avatar-button { border: 0; display: flex; align-items: center; gap: 5px; background: none; cursor: pointer; color: var(--text-muted); }
.avatar-button img { width: 30px; height: 30px; border: 1px solid var(--border); border-radius: 50%; object-fit: cover; }
.account-menu { display: flex; flex-direction: column; gap: 2px; }
.account-name { padding: 8px 10px 12px; border-bottom: 1px solid var(--border); margin-bottom: 4px; color: var(--text); font-weight: 600; }
.account-menu .el-button { margin: 0; border: 0; text-align: left; padding: 10px; background: transparent; color: var(--text); }
.account-menu .el-button:hover { background: var(--hover); }
.account-menu .logout-button { color: var(--danger); }
.header-slot { justify-content: space-between; display: flex; align-items: center; flex: 1; min-width: 0; gap: 16px; }
.mobile-menu-toggle { display: none; }
@media (max-width: 1100px) { .header-inner { gap: 16px; padding: 0 24px; } .header-navigation { gap: 16px; } .header-navigation > a i { display: none; } .header-account { gap: 8px; } }
@media (max-width: 900px) {
  .header-inner { padding: 0 16px; gap: 12px; } .header-divider { display: none; } .brand { font-size: 18px; } .brand-mark { width: 30px; height: 30px; }
  .mobile-menu-toggle { display: inline-flex; } .header-navigation { display: none; }
  .header-navigation.is-open { display: flex; position: absolute; top: 63px; left: 0; right: 0; height: auto; padding: 12px 16px 16px; background: var(--surface); border-bottom: 1px solid var(--border); box-shadow: 0 8px 16px #1f23280a; flex-wrap: wrap; gap: 6px; }
  .header-navigation > a { height: 38px; padding: 0 12px; border-radius: 6px; } .header-navigation > a.router-link-active { background: var(--hover); } .header-navigation > a.router-link-active::after { display: none; }
  .header-search { width: 100%; margin: 6px 0 0; height: 38px; flex-basis: 100%; }
}
@media (max-width: 700px) { .is-simple .header-account { display: none; } }
@media (max-width: 420px) { .header-inner { gap: 6px; padding: 0 12px; } .brand { gap: 7px; font-size: 16px; } .brand-mark { width: 28px; height: 28px; } .header-account { gap: 5px; } .register-link { padding: 5px 10px; } .icon-button { width: 28px; height: 30px; font-size: 17px; } .language-button { font-size: 11px; } .avatar-button { gap: 2px; padding: 0; } .avatar-button img { width: 28px; height: 28px; } }

</style>
