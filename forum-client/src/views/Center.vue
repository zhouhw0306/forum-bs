<template>
  <div>
    <el-card style="width: calc(100vw - 40px);margin: 19px">
      <el-avatar style="vertical-align: top" :size="90" :src="attachImageUrl(user.avatar)"></el-avatar>
      <div style="display: inline-block">
        <span style="margin: 10px;font-size: 30px;vertical-align: middle">{{user.username}}</span>
        <el-tag hit type="danger" size="mini" >会员</el-tag>
        <p style="color: #00000073;margin-left: 10px">简介: {{user.introduction}}</p>
      </div>
      <el-divider></el-divider>
      <div>
        <el-row>
          <el-col :span="3">
            <i class="el-icon-medal-1"></i> 积分 : {{user.score}}
          </el-col>
          <el-col :span="12">
            id : {{user.id}}
          </el-col>
        </el-row>
      </div>
    </el-card>
    <el-card style="width: calc(100vw - 40px);margin: 19px">
      <el-descriptions class="margin-top" title="基本信息" :column="3" border>
        <template slot="extra">
          <router-link to="/setting">
            <el-button type="primary" size="small">修改</el-button>
          </router-link>
        </template>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            用户名
          </template>
          {{ user.username }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-mobile-phone"></i>
            手机号
          </template>
          {{ user.phoneNum }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-outline"></i>
            居住地
          </template>
          <el-tag size="small">{{ user.location }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            性别
          </template>
          <el-tag size="small">{{ user.sex === 1 ? '男' : '女' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            邮箱
          </template>
          {{ user.email }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            生日
          </template>
          {{ user.birth }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            注册时间
          </template>
          {{ user.createTime }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            权限
          </template>
          {{ user.role }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            账号状态
          </template>
          {{ user.lockState === '0' ? '锁定':'启用' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card style="width: calc(50vw - 30px);margin: 0 9px 0 19px;display: inline-block;vertical-align: top">
        <p style="font-size: 16px;font-weight: 700;">资源收藏</p>
        <el-empty v-if="sources.length===0" :image-size="100" description="暂无数据"></el-empty>
        <div v-for="s in sources" style="margin: 15px">
          <router-link :to="`/details/${s.id}`">
            <a>{{s.title}}</a>
          </router-link>
        </div>
    </el-card>
    <el-card style="width: calc(50vw - 30px);margin: 0 19px 0 9px;display: inline-block;vertical-align: top">
      <p style="font-size: 16px;font-weight: 700;">文章收藏</p>
      <el-empty v-if="articles.length===0" :image-size="100" description="暂无数据"></el-empty>
      <div v-for="a in articles" style="margin: 15px">
        <router-link :to="`/view/${a.id}`">
          <a>{{a.title}}</a>
        </router-link>
      </div>
    </el-card>
    <el-card style="width: calc(50vw - 30px);margin: 20px 9px 0 19px;display: inline-block;vertical-align: top">
      <p style="font-size: 16px;font-weight: 700;">浏览记录</p>
      <el-empty v-if="browsingHistory.length===0" :image-size="100" description="暂无数据"></el-empty>
      <div v-for="a in browsingHistory" style="margin: 15px">
        <router-link :to="`/view/${a.id}`">
          <a>{{a.title}}</a>
        </router-link>
      </div>
    </el-card>
  </div>
</template>

<script>
import {mixin} from "@/mixins";
import {getArticleHasFavour, getAuthor, getBrowsingHistory, getSourceHasFavour} from "@/api";

export default {

  mixins: [mixin],
  data(){
    return {
      user : {},
      sources : [],
      articles : [],
      browsingHistory : []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init() {
      getAuthor().then(res => {
          this.user = res.data
      }).catch(err => {
          this.$message.error(err)
      })
      getSourceHasFavour().then(res => {
          this.sources = res.data
      }).catch(err => {
          this.$message.error(err)
      })
      getArticleHasFavour().then(res => {
          this.articles = res.data
      }).catch(err => {
          this.$message.error(err)
      })
      getBrowsingHistory().then(res => {
        this.browsingHistory = res.data
      }).catch(err => {
        this.$message.error(err)
      })
    },
  }
}


</script>

<style>

</style>