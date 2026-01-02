<template>
<div class="login-in">
  <div class="login">

    <div class="login-head">
      <span>帐号登录</span>
    </div>

    <el-form :model="loginForm" status-icon :rules="rules" ref="loginForm" class="demo-ruleForm" >
      <el-form-item prop="username">
        <el-input placeholder="用户名" v-model.trim="loginForm.username"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" placeholder="密码" v-model.trim="loginForm.password" @keyup.enter.native="loginIn" show-password></el-input>
      </el-form-item>
      <div class="login-btn">
        <el-button @click="goSignUp()">注册</el-button>
        <el-button type="primary" @click="handleLoginIn">登录</el-button>
      </div>
    </el-form>

    <div class="login-sns">
      <div class="login-sns-title">其它方式登录</div>
      <div style="display: flex">
        <div @click="githubLoginIn" class="login-sns-item"><img src="../assets/img/github.png" alt=""/>GitHub登录</div>
      </div>
    </div>

  </div>
</div>
</template>

<script>
import { mixin } from '@/mixins'
import {githubCallback, githubLoginIn, loginIn, allLikeCommonId} from '../api/index'

export default {
  name: 'LoginIn',
  mixins: [mixin],
  data () {
    let validateName = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('用户名不能为空'))
      } else {
        callback()
      }
    }
    let validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }
    return {
      loginForm: { // 登录用户名密码
        username: '',
        password: ''
      },
      rules: {
        username: [
          { validator: validateName, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    let {type}= this.$route.query
    if (type === 'github'){
      const loading = this.$loading({
        lock: true,
        text: '正在登录',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      githubCallback(this.$route.query).then(res=>{
        loading.close();
        if (res.code === 0) {
          this.notify('登录成功', 'success')
          this.setUserMsg(res.data)
          if(this.$route.query.redirect){
            this.$router.push({path: this.$route.query.redirect})
          }else{
            this.$router.push({path: '/'})
          }
        } else {
          this.notify(res.msg, 'error')
        }
      })
    }
  },
  methods: {
    handleLoginIn () {
      this.$refs.loginForm.validate(validate=>{
        if (!validate) return;
        let _this = this
        let params = new URLSearchParams()
        params.append('username', this.loginForm.username)
        params.append('password', this.loginForm.password)
        loginIn(params)
            .then(res => {
              if (res.code === 0) {
                _this.notify('登录成功', 'success')
                _this.setUserMsg(res.data)
                if(this.$route.query.redirect){
                  _this.$router.push({path: this.$route.query.redirect})
                }else{
                  _this.$router.push({path: '/'})
                }
              } else {
                _this.notify(res.msg, 'error')
              }
            })
      })
    },
    setUserMsg (item) {
      this.$store.commit('setLoginIn', true) //是否登录
      this.$store.commit('setUserId', item.id)  //用户id
      this.$store.commit('setUsername', item.username)  //用户名
      this.$store.commit('setAvatar', item.avatar)  //头像url
      this.$store.commit('setRole', item.role)  //身份
      this.$store.commit('setToken',item.token) //存储用户信息到浏览器
      allLikeCommonId().then(res => {
        this.$store.commit('setLikedComments', res.data)
      })
    },
    goSignUp () {
      this.$router.push({path: '/register'})
    },
    githubLoginIn (){
      githubLoginIn().then(res => {
        location.href = res.data
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@use '../assets/css/login-in.scss';

.login-sns{
  color: #9499a0;
  font-weight: 400;
  font-size: 13px;
  line-height: 16px;
}
.login-sns-title {
  text-align: center;
  margin: 10px 0;
}

.login-sns-item:hover{
  cursor: pointer;
}
.login-sns-item img{
  width: 25px;
  vertical-align: middle;
  margin-left: 5px
}
</style>