<template>
  <div class="signup-page"><div class="signup-container">
    <div class="signup-heading"><h1>加入 CodeBase</h1><p>创建账号，与开发者分享知识和灵感。</p></div>
    <div class="signup-card"><el-form :model="registerForm" :rules="rules" ref="registerForm" label-position="top" @submit.native.prevent="SignUp">
      <el-form-item prop="username" label="用户名"><el-input v-model="registerForm.username" @focus="clearValida('username')" placeholder="设置你的用户名" autocomplete="username" /></el-form-item>
      <el-form-item prop="password" label="密码"><el-input v-model="registerForm.password" type="password" @focus="clearValida('password')" placeholder="设置登录密码" autocomplete="new-password" show-password /></el-form-item>
      <el-row :gutter="20"><el-col :xs="24" :sm="12"><el-form-item prop="sex" label="性别"><el-radio-group v-model="registerForm.sex"><el-radio :label="0">女</el-radio><el-radio :label="1">男</el-radio></el-radio-group></el-form-item></el-col><el-col :xs="24" :sm="12"><el-form-item prop="birth" label="生日"><el-date-picker type="date" placeholder="选择日期" v-model="registerForm.birth" style="width: 100%" /></el-form-item></el-col></el-row>
      <el-form-item prop="location" label="所在地区"><el-select v-model="registerForm.location" placeholder="选择地区" style="width: 100%"><el-option v-for="item in cities" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item prop="email" label="邮箱"><el-input v-model="registerForm.email" @focus="clearValida('email')" placeholder="输入你的邮箱地址" autocomplete="email" /></el-form-item>
      <el-form-item prop="checkCode" label="邮箱验证码"><div class="verification-row"><el-input v-model="registerForm.checkCode" @focus="clearValida('checkCode')" placeholder="输入验证码" autocomplete="one-time-code" /><el-button @click="sendCode" :disabled="btnCode">{{ djs }}</el-button></div></el-form-item>
      <el-button type="primary" native-type="submit" class="signup-submit">创建账号</el-button>
    </el-form></div>
    <p class="signup-alternative">已经有账号？ <router-link to="/login">登录 CodeBase</router-link></p>
  </div></div>
</template>
<script>
import { mixin } from '@/mixins'
import { rules, cities } from '@/assets/data/form'
import { SignUp,ApiSendCode } from '../api/index'

export default {
  name: 'SignUp',
  mixins: [mixin],
  data () {
    return {
      btnCode:false,
      djs : '发送验证码', //倒计时数字
      registerForm: { // 注册
        username: '',
        password: '',
        sex: '',
        email: '',
        checkCode:'',
        location: '',
        birth: ''
      },
      rules: {}, //表单验证
      cities: [], //地区
    }
  },
  created () {
    this.rules = rules //表单验证
    this.cities = cities
  },
  methods: {
    // 发送验证码
    sendCode(){
      this.btnCode=true
      if (!this.registerForm.email){
        this.$message.warning('邮箱不能为空');
        this.btnCode=false
        return
      }
      const reg = /^([a-zA-Z0-9]+[-_\.]?)+@[a-zA-Z0-9]+\.[a-z]+$/;
      if (!reg.test(this.registerForm.email)){
        this.$message.warning('请输入正确的邮箱格式');
        this.btnCode=false
        return
      }
      let params = new URLSearchParams()
      params.append('email',this.registerForm.email)
      ApiSendCode(params).then( res => {
        if (res.code === 0) {
          this.$message.success("验证码发送成功");
          this.nextNum()
        }else {
          this.$message.error(res.msg);
          this.btnCode=false
        }
      }).catch(err => {
        this.$message.error("验证码发送失败"+err)
        this.btnCode=false
      })
    },
    //倒计时
    nextNum(){
      let i = 60;     //倒计时时间
      let timer = setInterval(() => {
        this.djs = i + "s";
        i--;
        if (i < 0) {
          this.btnCode=false
          this.djs='发送验证码'
          clearInterval(timer);
        }
      }, 1000);
    },
    // 注册
    SignUp () {
      let _this = this
      _this.$refs.registerForm.validate(validate=>{
        if (!validate) return;
        let datetime = '';
        if (this.registerForm.birth !== ''){
          let d = this.registerForm.birth
          datetime = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()
        }
        let params = new URLSearchParams()
        params.append('username', this.registerForm.username)
        params.append('password', this.registerForm.password)
        params.append('sex', this.registerForm.sex)
        params.append('email', this.registerForm.email)
        params.append('checkCode', this.registerForm.checkCode)
        params.append('birth', datetime)
        params.append('location', this.registerForm.location)
        params.append('avatar', '/avatarImages/user.jpg')
        SignUp(params)
            .then(res => {
              if (res.code === 0) {
                _this.notify('注册成功', 'success')
                _this.$router.push({path: '/login'})
              } else {
                _this.notify(res.msg, 'error')
              }
            })
      })
    },
    clearValida(data){
      this.$refs.registerForm.clearValidate([data])
    },
    //取消
    goBack () {
      this.$router.go(-1)
    }
  }
}
</script>
<style scoped>

.signup-page { padding: 40px 16px; display: flex; justify-content: center; }
.signup-container { width: 440px; max-width: 100%; }
.signup-heading { margin-bottom: 24px; text-align: center; } .signup-heading h1 { font-size: 24px; font-weight: 500; letter-spacing: -.5px; } .signup-heading p { color: var(--text-muted); margin-top: 8px; font-size: 13px; }
.signup-card { background: var(--surface); border: 1px solid var(--border); border-radius: 6px; padding: 24px; }
.signup-card >>> .el-form-item { margin-bottom: 20px; } .signup-card >>> .el-form-item__label { padding-bottom: 6px; line-height: 1.5; } .signup-card >>> .el-radio-group { min-height: 40px; display: flex; align-items: center; gap: 24px; } .signup-card >>> .el-radio { margin-right: 0; }
.verification-row { display: flex; align-items: center; gap: 10px; } .verification-row .el-input { min-width: 0; flex: 1; } .verification-row .el-button { padding: 12px; flex-shrink: 0; }
.signup-submit { width: 100%; margin-top: 4px; } .signup-alternative { padding: 20px 0 0; text-align: center; font-size: 13px; color: var(--text-muted); }
@media (max-width: 600px) { .signup-page { padding: 32px 16px; } .signup-card { padding: 20px; } }

</style>
