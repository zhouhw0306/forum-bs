<template>
<div class="signUp-page">
<!--  <img src="../assets/favicon.ico" width="1px" height="710px">-->
  <div class="signUp">
    <div class="signUp-head">
      <span>用户注册</span>
    </div>
    <el-form :model="registerForm" status-icon :rules="rules" ref="registerForm" label-width="70px" class="demo-ruleForm">
      <el-form-item prop="username" label="用户名">
        <el-input v-model="registerForm.username" placeholder="用户名"></el-input>
      </el-form-item>
      <el-form-item prop="password" label="密码">
        <el-input type="password" placeholder="密码" v-model="registerForm.password" show-password></el-input>
      </el-form-item>
      <el-form-item prop="sex" label="性别">
        <el-radio-group v-model="registerForm.sex">
          <el-radio :label="0">女</el-radio>
          <el-radio :label="1">男</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item prop="birth" label="生日">
        <el-date-picker type="date" placeholder="选择日期" v-model="registerForm.birth" style="width: 100%;"></el-date-picker>
      </el-form-item>
      <el-form-item prop="location" label="地区">
        <el-select v-model="registerForm.location" placeholder="地区" style="width:100%">
          <el-option v-for="item in cities" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="email" label="邮箱">
        <el-input v-model="registerForm.email" placeholder="邮箱"></el-input>
      </el-form-item>

      <el-row >
        <el-col :span="15">
          <el-form-item prop="checkCode" label="验证码">
            <el-input v-model="registerForm.checkCode" placeholder="验证码" :style="{width: '90%'} "></el-input>
          </el-form-item>
        </el-col>

        <el-col :span="3">
          <el-form-item label-width="10px">
            <el-button @click="sendCode" type="primary" round :disabled="btnCode">{{djs}}</el-button>
          </el-form-item>
        </el-col>
      </el-row>

      <div class="login-btn">
        <el-button @click="goback(-1)">取消</el-button>
        <el-button type="primary" @click="SignUp">确定</el-button>
      </div>
    </el-form>
  </div>
</div>
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
    sendCode(){
      this.btnCode=true
      if (this.registerForm.email=='' || this.registerForm.email==null){
        this.$message({
          message: '邮箱不能为空',
          type: 'warning'
        });
        this.btnCode=false
        return
      }
      const reg = /^([a-zA-Z0-9]+[-_\.]?)+@[a-zA-Z0-9]+\.[a-z]+$/;
      if (!reg.test(this.registerForm.email)){
        this.$message({
          message: '请输入正确的邮箱格式',
          type: 'warning'
        });
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

    SignUp () {
      if (this.registerForm.username===''){
        this.$message.error("用户名不能为空");
        return
      }
      if (this.registerForm.password===''){
        this.$message.error("密码不能为空");
        return
      }
      if (this.registerForm.sex===''){
        this.$message.error("性别不能为空");
        return
      }
      if (this.registerForm.email===''){
        this.$message.error("邮箱不能为空");
        return
      }
      if (this.registerForm.checkCode===''){
        this.$message.error("验证码不能为空");
        return
      }
      let _this = this
      let datetime = '';
      if (this.registerForm.birth != ''){
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
        .catch(err => {
          console.log(err)
        })
    },
    goback (index) {
      this.$router.go(index)
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/sign-up.scss';
</style>
