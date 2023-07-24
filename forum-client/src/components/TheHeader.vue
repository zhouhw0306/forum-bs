<template>
  <el-header class="me-area">

    <el-row class="me-header">

      <el-col :span="4" class="me-header-left">
        <router-link to="/" class="me-title">
          <img src="../assets/logo.png"/>
          <span> codebase</span>
        </router-link>
      </el-col>
      <el-col v-if="!simple" :span="16">
        <el-menu :router=true menu-trigger="click" active-text-color="#409EFF" :default-active="activeIndex"
                 mode="horizontal">
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
      </el-col>

      <template v-else>
        <slot></slot>
      </template>

      <el-col :span="4">
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
                <el-popover
                    :visible-arrow="false"
                    placement="bottom"
                    width="150"
                    trigger="hover">
                    <div>
                      <el-button class="btt" @click="setting"><i class="el-icon-user icon"></i>个人资料</el-button>
                      <el-button class="btt" @click="write" style="margin-left: 0"><i class="el-icon-edit icon"></i>发帖</el-button>
<!--                      <el-button class="btt" style="margin-left: 0"><i class="el-icon-user icon"></i>内容管理</el-button>-->
                      <el-button class="btt" @click="logout" style="margin-left: 0;color: #e86f6f"><i class="el-icon-switch-button icon"></i>退出</el-button>
                    </div>
                  <img slot="reference" class="me-header-picture" :src="attachImageUrl(avatar)"/>
                </el-popover>
          </template>
        </el-menu>
      </el-col>

    </el-row>

  </el-header>
</template>

<script>
import {mixin} from "@/mixins";
import { mapGetters } from 'vuex';
import {searchData} from '@/api';

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
    return {
      inputValue: "", //搜索框输入
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
  methods: {
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
      this.$store.commit('setLoginIn', false) //是否登录
      this.$store.commit('setUserId', '')  //用户id
      this.$store.commit('setUsername', '')  //用户名
      this.$store.commit('setAvatar', '')  //头像url
      this.$store.commit('setRole', '')  //身份
      this.$store.commit('setToken','') //存储用户信息到浏览器
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
  z-index: 1024;
  min-width: 100%;
  box-shadow: 0 2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
}
.me-header{
  background: rgba(255,255,255,0.7);
}
.me-title {
  font-size: 22px;
  color: #409EFF;
}

.me-header-left {
  margin-top: 10px;
  white-space: nowrap;
}

.me-title img {
  max-height: 2.4rem;
  max-width: 100%;
  vertical-align: bottom
}

.me-header-picture {
  width: 40px;
  height: 40px;
  border: 1px solid #4377fc;
  border-radius: 50%;
  vertical-align: middle;
  background-color: #409EFF;
  margin-left: 30px;
}
.me-header-picture:hover{
  width: 50px;
  height: 50px;
  transition: all 0.3s ease 0s;
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
</style>
