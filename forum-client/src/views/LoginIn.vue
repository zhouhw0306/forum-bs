<template>
  <div class="auth-page"><div class="auth-container">
    <div class="auth-heading"><span class="auth-symbol"><i class="el-icon-chat-dot-square"></i></span><h1>登录 CodeBase</h1><p>欢迎回来，继续你的探索与分享。</p></div>
    <div class="auth-card">
      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-position="top" @submit.native.prevent="handleLoginIn">
        <el-form-item prop="username" label="用户名" for="login-username"><el-input id="login-username" v-model.trim="loginForm.username" placeholder="请输入用户名" autocomplete="username" /></el-form-item>
        <el-form-item prop="password" label="密码" for="login-password"><el-input id="login-password" type="password" v-model.trim="loginForm.password" placeholder="请输入密码" autocomplete="current-password" show-password /></el-form-item>
        <el-button type="primary" class="auth-submit" native-type="submit">登录</el-button>
      </el-form>
      <div class="auth-divider"><span>或使用其他方式</span></div>
      <button class="github-login" @click="githubLoginIn"><i class="fa fa-github"></i> 使用 GitHub 登录</button>
    </div>
    <div class="auth-alternative">初次来到 CodeBase？ <router-link to="/register">创建账号</router-link></div>
    <router-link to="/" class="auth-back"><i class="el-icon-arrow-left"></i> 返回社区</router-link>
  </div></div>
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
<style scoped>

.auth-page { padding: 48px 16px; display: flex; align-items: center; justify-content: center; }
.auth-container { width: 360px; max-width: 100%; }
.auth-heading { text-align: center; margin-bottom: 24px; }
.auth-symbol { display: inline-flex; justify-content: center; align-items: center; width: 48px; height: 48px; border-radius: 12px; color: var(--text); font-size: 36px; margin-bottom: 16px; }
.auth-heading h1 { font-size: 24px; font-weight: 500; letter-spacing: -.5px; } .auth-heading p { font-size: 13px; color: var(--text-muted); margin-top: 8px; }
.auth-card { background: var(--surface); border: 1px solid var(--border); padding: 24px; border-radius: 6px; }
.auth-card >>> .el-form-item { margin-bottom: 20px; } .auth-card >>> .el-form-item__label { padding-bottom: 6px; line-height: 1.5; }
.auth-submit { width: 100%; margin-top: 2px; }
.auth-divider { display: flex; align-items: center; gap: 12px; margin: 24px 0 18px; color: var(--text-muted); font-size: 11px; } .auth-divider::before, .auth-divider::after { content: ''; height: 1px; background: var(--border-subtle); flex: 1; }
.github-login { display: flex; align-items: center; justify-content: center; gap: 8px; width: 100%; padding: 9px; border: 1px solid var(--border); border-radius: 6px; background: var(--surface-muted); color: var(--text); font-size: 13px; font-weight: 500; cursor: pointer; } .github-login:hover { background: var(--hover); } .github-login i { font-size: 19px; }
.auth-alternative { text-align: center; padding: 18px 12px; border: 1px solid var(--border); border-radius: 6px; margin-top: 16px; font-size: 13px; }
.auth-back { display: block; text-align: center; color: var(--text-muted); font-size: 12px; margin-top: 24px; }
@media (max-width: 600px) { .auth-page { padding: 32px 16px; } }

</style>
