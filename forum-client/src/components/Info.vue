<template>
  <div class="info">
    <p class="title">编辑个人资料</p>
    <hr/>
    <div class="personal">
      <el-form :model="registerForm" class="demo-ruleForm" label-width="80px">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="registerForm.username" placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="registerForm.sex">
            <el-radio :label="0">女</el-radio>
            <el-radio :label="1">男</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="phoneNum" label="手机">
          <el-input  placeholder="手机" v-model="registerForm.phoneNum" ></el-input>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="registerForm.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item prop="birth" label="生日">
          <el-date-picker type="date" placeholder="选择日期" v-model="registerForm.birth" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item prop="introduction" label="签名">
          <el-input  type="textarea" placeholder="签名" v-model="registerForm.introduction" ></el-input>
        </el-form-item>
        <el-form-item prop="location" label="地区">
          <el-select v-model="registerForm.location" placeholder="地区" style="width:100%">
            <el-option
              v-for="item in cities"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
    <div class="btn">
      <div @click="saveMsg">保存</div>
      <div @click="goBack">取消</div>
    </div>
    </div>
</div>
</template>

<script>
import { mapGetters } from 'vuex'
import { cities} from '../assets/data/form'
import { updateUser, getAuthor } from '../api/index'

export default {
  name: 'info',
  data: function () {
    return {
      registerForm: { // 表单
        id:'',
        username: '',
        sex: '',
        phoneNum: '',
        email: '',
        birth: '',
        introduction: '',
        location: ''
      },
      cities: []
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ])
  },
  created () {
    this.cities = cities
  },
  mounted () {
    this.getUser()
  },
  methods: {
    getUser () {
      getAuthor()
        .then(res => {
          this.registerForm = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    goBack () {
      this.$router.go(-1)
    },
    saveMsg () {
      this.$confirm('是否更新个人信息?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateUser(this.registerForm)
            .then(res => {
              if (res.code === 0) {
                this.$store.commit('setUsername', this.registerForm.username)
                this.$notify.success({
                  title: '更新成功',
                  showClose: true
                })
                setTimeout(function () {
                  this.$router.go(-1)
                }, 2000)
              } else {
                this.$notify.error({
                  title: '更新失败',
                  showClose: true
                })
              }
            })
            .catch(err => {
              console.log(err)
            })
      }).catch(() => {
        this.getMsg(this.userId)
        this.$message({
          type: 'info',
          message: '已取消更新'
        });
      });

    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/info.scss';
</style>
