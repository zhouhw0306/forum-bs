<template>
  <el-header class="me-area">

    <el-row class="me-header">

      <el-col :span="4" class="me-header-left">
        <router-link to="/" class="me-title">
          <img style="vertical-align: bottom" src="../assets/logo.png"/>
          <span style="color: black"> 编程社区</span>
        </router-link>
      </el-col>
      <el-col v-if="!simple" :span="16">
        <el-menu :router=true menu-trigger="click" active-text-color="#409EFF" :default-active="activeIndex"
                 mode="horizontal">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/CarePost" @click="toCarePost">关注</el-menu-item>
          <el-menu-item index="/source">资源</el-menu-item>
          <el-menu-item index="/world">世界</el-menu-item>
          <el-menu-item index="/nav">编程导航</el-menu-item>
          <el-menu-item v-if="role === 'ADMIN'" index="/userManage">管理</el-menu-item>
          <div>
            <el-input placeholder="请输入想要搜索的内容" v-model="searchUser" class="el-search">
              <i
                  slot="suffix"
                  style="cursor: pointer"
                  class="el-input__icon el-icon-search"
                  @click="selUser"
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
                <el-popover
                    :visible-arrow="false"
                    placement="bottom"
                    width="150"
                    trigger="hover">
                  <div>
                    <div style="justify-content: center">
                      <el-button class="btt" @click="setting"><i class="el-icon-user icon"></i>个人资料</el-button>
                      <el-button class="btt" @click="logout"><i class="el-icon-switch-button icon"></i>退出</el-button>
                    </div>
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
    return {
      searchUser: "" //搜索框输入
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
    selUser() {},//搜索触发事件
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
      localStorage.removeItem('role')
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

<style scoped>
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
  font-size: 22px;
}

.me-header-left {
  margin-top: 10px;
  white-space: nowrap;
}

.me-title img {
  max-height: 2.4rem;
  max-width: 100%;
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
  border-bottom: #2aa3ef solid 2px !important;
}
.el-search{
  width: 200px;
  margin-right: 20px;
  float: right;
  transition: all 0.5s ease 0s;
}
.el-search:hover{
  width: 300px;
}
.el-menu.el-menu--horizontal{
  line-height: 60px;
  border: none;
}
.btt{
  margin-left: 0px;
  width: 100%;
  border:none;
}
.icon{
  float: left;
}
</style>
