<template>
  <el-header class="me-area">

    <el-row class="me-header">

      <el-col :span="4" class="me-header-left">
        <router-link to="/" class="me-title">
          <img style="vertical-align: bottom" src="../assets/logo.png"/>
          <span style="color: #ff843c"> 编程社区</span>
        </router-link>
      </el-col>
      <el-col v-if="!simple" :span="16">
        <el-menu :router=true menu-trigger="click" active-text-color="#409EFF" :default-active="activeIndex"
                 mode="horizontal">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/carePost" @click="toCarePost">我的关注</el-menu-item>
          <el-menu-item index="/type/all">分类</el-menu-item>
          <el-menu-item index="/log">日志</el-menu-item>
          <el-menu-item index="/schoolBoard">留言墙</el-menu-item>
          <el-menu-item index="/nav">编程导航</el-menu-item>
<!--          <el-col :span="4" :offset="5">-->
<!--            <el-menu-item style="margin-left: 10%" @click="toWrite"><i class="el-icon-edit"></i>发帖</el-menu-item>-->
<!--          </el-col>-->
        </el-menu>
      </el-col>

      <template v-else>
        <slot></slot>
      </template>

      <el-col :span="4">
        <el-menu :router=true menu-trigger="click" mode="horizontal" active-text-color="#409EFF">

          <template v-if="!loginIn">
            <el-tooltip class="item" effect="dark" content="登录后可发帖和评论" placement="bottom">
              <el-menu-item index="/login">
                <el-button type="text">登录</el-button>
              </el-menu-item>
            </el-tooltip>
            <el-menu-item index="/register">
              <el-button type="text">注册</el-button>
            </el-menu-item>
          </template>

          <template v-else>
            <el-submenu index>
              <template slot="title">
                <img class="me-header-picture" :src="attachImageUrl(avatar)"/>
              </template>
              <el-menu-item index @click="setting"><i class="el-icon-back"></i>个人资料</el-menu-item>
              <el-menu-item index @click="logout"><i class="el-icon-back"></i>退出</el-menu-item>
            </el-submenu>

          </template>
        </el-menu>
      </el-col>

    </el-row>

  </el-header>

</template>

<script>
import {mixin} from "@/mixins";
import { mapGetters } from 'vuex'

export default {
  name: 'TheHeader',
  mixins: [mixin],
  props: {
    activeIndex: String,
    simple: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters([
      'userId',
      'avatar',
      'username',
      'loginIn'
    ]),
  },
  methods: {
    toWrite(){
      if (!this.$store.getters.loginIn){
        this.$message({type: 'error', message: '请先登录', showClose: true})
        return;
      }
      this.$router.push({path: '/write'})
    },
    logout() {
      localStorage.removeItem('loginIn') //登录状态
      localStorage.removeItem('userId')  //用户id
      localStorage.removeItem('username') //用户名
      localStorage.removeItem('avatar') //头像url
      localStorage.removeItem('token')
      this.$router.go(0)
      this.notify("退出成功",'success')
    },
    setting() {
      if(this.loginIn){
        this.$router.push({path:'/setting'})
      }
    },
    toCarePost(){
      if (!this.$store.getters.loginIn){
        this.$router.push({path : '/login'})
      }else {
        this.$router.push({path : '/carePost'})
      }
    }
  }
}
</script>

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
  z-index: 1024;
  min-width: 100%;
  box-shadow: 0 2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
}
.me-header{
  background: rgba(255,255,255,0.7);
}
.me-title {
  margin-top: 10px;
  font-size: 24px;
}

.me-header-left {
  margin-top: 10px;
}

.me-title img {
  max-height: 2.4rem;
  max-width: 100%;
}

.me-header-picture {
  width: 36px;
  height: 36px;
  border: 1px solid #4377fc;
  border-radius: 50%;
  vertical-align: middle;
  background-color: #409EFF;
}
</style>
