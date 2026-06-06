<template>
  <el-header class="me-area">

    <el-row class="me-header">

      <div class="me-header-left">
        <router-link to="/" class="me-title">
          <img src="../assets/logo.png"/>
          <span class="me-logo-text"> codebase</span>
        </router-link>
        <span class="me-logo-arrow" @click.stop="toggleMobileMenu">
          <i :class="mobileMenu ? 'el-icon-close' : 'el-icon-caret-bottom'"></i>
        </span>
      </div>

      <div v-if="!simple" class="me-nav-wrap" :class="{ 'mobile-open': mobileMenu }">
        <el-menu :router=true menu-trigger="click" active-text-color="#409EFF" :default-active="activeIndex"
                 mode="horizontal" class="main-nav-menu">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/source?type=工具&sort=create_time&pageNo=1">资源</el-menu-item>
          <el-menu-item v-if="loginIn" index="/carePost">关注</el-menu-item>
          <el-menu-item v-if="loginIn" index="/center">个人中心</el-menu-item>
          <el-menu-item index="/searchPage">搜索</el-menu-item>
          <el-menu-item v-if="role === 'ADMIN'" index="/info">管理</el-menu-item>
          <div>
            <el-input @keyup.enter.native="toSearch" v-model="inputValue" placeholder="请输入想要搜索的内容" class="el-search">
              <i
                  slot="suffix"
                  style="cursor: pointer"
                  class="el-input__icon el-icon-search"
                  @click="toSearch"
              ></i>
            </el-input>
          </div>
        </el-menu>
      </div>

      <template v-else>
        <slot></slot>
      </template>

      <div class="me-header-right">
        <el-menu :router=true menu-trigger="click" mode="horizontal" active-text-color="#409EFF">
          <template v-if="!loginIn">
            <el-tooltip class="item" effect="dark" content="登录查看更多内容" placement="bottom">
              <el-menu-item index="/login">
                <el-button type="text">登录</el-button>
              </el-menu-item>
            </el-tooltip>
            <el-menu-item index="/register">
              <el-button type="text">注册</el-button>
            </el-menu-item>
          </template>

          <template v-else>
            <div class="header-actions">
              <!-- 用户头像 -->
              <el-popover
                :visible-arrow="false"
                placement="bottom"
                width="150"
                trigger="hover"
              >
                <div>
                  <el-button class="btt" @click="setting"><i class="el-icon-user icon"></i>个人资料</el-button>
                  <el-button class="btt" @click="write" style="margin-left: 0"><i class="el-icon-edit icon"></i>发帖</el-button>
                  <el-button class="btt" @click="logout" style="margin-left: 0;color: #e86f6f"><i class="el-icon-switch-button icon"></i>退出</el-button>
                </div>
                <span slot="reference" class="header-action-btn header-action-avatar-wrap">
                  <img :src="attachImageUrl(avatar)"/>
                </span>
              </el-popover>

              <!-- 通知 -->
              <el-popover
                placement="bottom"
                width="360"
                trigger="click"
                v-model="notifyVisible"
                @show="onNotifyPanelOpen"
              >
                <NotificationCenter
                  ref="notifyCenter"
                  @read="onNotifyRead"
                  @read-all="onNotifyReadAll"
                  @delete="onNotifyRead"
                />
                <span slot="reference" class="header-action-btn">
                  <i class="el-icon-bell"></i>
                  <sup v-if="unreadCount > 0" class="notify-dot">{{ unreadCount > 99 ? '99+' : unreadCount }}</sup>
                </span>
              </el-popover>

              <!-- AI -->
              <el-tooltip content="DeepSeek AI" placement="bottom" effect="light">
                <span class="header-action-btn" @click="drawer = true">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                  </svg>
                </span>
              </el-tooltip>
            </div>

            <el-drawer
                title="DeepSeek AI"
                :visible.sync="drawer"
                size="50%"
                direction="rtl"
                :append-to-body="true"
                :modal-append-to-body="false">
                <AiModel/>
            </el-drawer>
          </template>
        </el-menu>
      </div>

    </el-row>

  </el-header>
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
  methods: {
    toggleMobileMenu() { this.mobileMenu = !this.mobileMenu; },
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
.el-menu-item:hover{
  border-bottom: #2aa3ef solid 2px !important;
}
</style>
<style>
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.demonstration {
  display: block;
  color: #8492a6;
  font-size: 14px;
  margin-bottom: 20px;
}
.el-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  z-index: 1024;
  box-shadow: 0 2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
}
.me-title {
  font-size: 22px;
  color: #409EFF;
}

.me-header {
  background: rgba(255,255,255,0.7);
  display: flex;
  align-items: center;
}
.me-header-left {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
.me-nav-wrap { flex: 1; min-width: 0; overflow: hidden; }
.me-header-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  margin-left: auto !important;
}

/* 覆盖 el-row 默认样式 */
.el-row.me-header {
  flex-wrap: nowrap !important;
  margin: 0 !important;
}
.me-logo-text { white-space: nowrap; }
.me-logo-arrow {
  display: none;
  font-size: 18px;
  color: #555;
  cursor: pointer;
  padding: 0 8px;
}
.me-logo-arrow:hover { color: #409eff; }

/* ---- 右侧操作区（头像 / 通知 / AI）---- */
.header-actions {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  height: 60px;
}
.header-action-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 80px;
  border-radius: 12px;
  color: #555;
  cursor: pointer;
  font-size: 20px;
  transition: color 0.2s, background 0.2s;
}
.header-action-btn:hover {
  color: #409eff;
  background: #ecf5ff;
}
.notify-dot {
  position: absolute;
  top: 0;
  right: 0;
  height: 17px;
  min-width: 17px;
  line-height: 17px;
  padding: 0 5px;
  border-radius: 9px;
  background: #f56c6c;
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  transform: translate(30%, -30%);
  pointer-events: none;
}
.header-action-avatar-wrap {
  border-radius: 50% !important;
  background: transparent !important;
  padding: 0 !important;
}
.header-action-avatar-wrap img {
  display: block;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e8eaed;
  transition: transform 0.3s cubic-bezier(.34,1.56,.64,1),
              box-shadow 0.3s ease,
              border-color 0.3s ease;
}
.header-action-avatar-wrap:hover {
  background: transparent !important;
}
.header-action-avatar-wrap:hover img {
  transform: scale(1.18);
  border-color: #409eff;
  box-shadow:
    0 0 0 3px rgba(64,158,255,.2),
    0 4px 16px rgba(64,158,255,.25);
}
.me-title img {
  max-height: 2.4rem;
  max-width: 100%;
  vertical-align: bottom;
}
.el-menu-item:hover{
  color: #1787FB !important;
}
.el-search{
  width: 200px;
  margin-right: 20px;
  float: right;
  transition: all 0.5s ease 0s;
}
.el-menu.el-menu--horizontal{
  line-height: 60px;
  border: none;
}
.btt{
  width: 100%;
  border:none;
}
.icon{
  float: left;
}
.me-pull-right {
  float: right;
}
.me-article-count {
  color: #a6a6a6;
  padding-left: 14px;
  font-size: 13px;
}
.el-scrollbar__wrap{
  overflow-x: hidden;
}
.el-scrollbar__bar.is-horizontal {
  display: none;
}
/* ---- 响应式 ---- */
@media (max-width: 900px) {
  .me-logo-text { display: none !important; }
  .me-logo-arrow { display: flex !important; align-items: center; height: 60px; }
  .me-nav-wrap { display: none !important; }
  .me-nav-wrap.mobile-open {
    display: block !important;
    position: absolute;
    top: 60px;
    left: 0;
    right: 0;
    background: #fff;
    box-shadow: 0 4px 12px rgba(0,0,0,.1);
    z-index: 2000;
  }
  .me-nav-wrap.mobile-open .el-menu {
    display: flex !important;
    flex-direction: column;
    border: none;
  }
  .me-nav-wrap.mobile-open .el-menu .el-menu-item {
    height: 46px !important;
    line-height: 46px !important;
  }
  .el-search { width: 140px !important; margin-right: 8px; }
  .header-action-btn { width: 36px !important; height: 36px !important; font-size: 18px !important; }
  .header-action-avatar-wrap img { width: 26px !important; height: 26px !important; }
  .el-header { padding: 0 12px !important; }
}

@media (max-width: 600px) {
  .el-header { padding: 0 8px !important; }
  .me-title img { max-height: 28px; }
  .el-search { width: 100px !important; margin-right: 4px; font-size: 12px; }
  .header-actions { gap: 6px; }
  .header-action-btn { width: 32px !important; height: 32px !important; font-size: 16px !important; border-radius: 8px; }
  .header-action-avatar-wrap img { width: 24px !important; height: 24px !important; }
  .notify-dot { height: 14px; min-width: 14px; line-height: 14px; font-size: 9px; padding: 0 4px; }
}
</style>
