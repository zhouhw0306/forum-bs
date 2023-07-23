<template>
  <div>
    <el-row :gutter="20" class="sep">
      <el-col :span="6">
        <el-card style="background-color:#7ed454" shadow="hover" :body-style="{padding: '0px'}">
          <div class="grid-content grid-con-1">
            <div class="grid-cont-right">
              <div class="grid-num">{{userCount}}</div>
              <div>用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background-color:#5ea9f6" shadow="hover" :body-style="{padding: '0px'}">
          <div class="grid-content grid-con-2">
            <div class="grid-cont-right">
              <div class="grid-num">{{newCustomer}}</div>
              <div>昨日新增</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background-color:#ea6f6f" shadow="hover" :body-style="{padding: '0px'}">
          <div class="grid-content grid-con-3">
            <div class="grid-cont-right">
              <div class="grid-num">{{articleCount}}</div>
              <div>帖子数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background-color:#eeb45d" shadow="hover" :body-style="{padding: '0px'}">
          <div class="grid-content grid-con-4">
            <div class="grid-cont-right">
              <div class="grid-num">{{sourceCount}}</div>
              <div>资源数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>


    <el-row :gutter="20">
      <el-col :span="12">
        <h3 style="margin-bottom: 20px">用户性别比例</h3>
        <div  style="background-color: white">
          <ve-pie :data="userSex" :theme="options1"></ve-pie>
        </div>
      </el-col>
      <el-col :span="12">
        <h3 style="margin-bottom: 20px">用户地区分布</h3>
        <div  style="background-color: white">
          <ve-histogram :data="userArea" :theme="options2"></ve-histogram>
        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script>

import { mixin } from '@/mixins'
import { getAllUser, getSourceAll, getAllNoPage} from '@/api'

export default {
  mixins: [mixin],
  data () {
    return {
      userCount: 0,
      newCustomer: 0,
      articleCount: 0,
      sourceCount: 0,
      user: [],
      userSex: {
        columns: ['性别', '总数'],
        rows: [
          { '性别': '男', '总数': 0 },
          { '性别': '女', '总数': 0 }
        ]
      },
      userArea: {
        columns: ['地区', '总数'],
        rows: [
          { '地区': '江西', '总数': 0 },
          { '地区': '江苏', '总数': 0 },
          { '地区': '广东', '总数': 0 },
          { '地区': '福建', '总数': 0 },
          { '地区': '上海', '总数': 0 },
          { '地区': '北京', '总数': 0 },
          { '地区': '湖北', '总数': 0 },
          { '地区': '浙江', '总数': 0 },
          { '地区': '台湾', '总数': 0 },
          { '地区': '香港', '总数': 0 },
          { '地区': '澳门', '总数': 0 },
          { '地区': '其他', '总数': 0 }
        ]
      },
      options1: {
        color: ['#5da8f5', '#ef8888']
      },
      options2: {
        color: ['#FD8A61'],
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        }
      }
    }
  },
  mounted () {
    this.getUser()
    this.getSourceCount()
    this.getArticleCount()
  },
  methods: {
    getSourceCount(){
      getSourceAll('1').then(res => {
        if (res.code===0){
          this.sourceCount = res.data.length
        }else {
          this.$message.error('资源数量加载失败')
        }
      }).catch(err => this.$message.error(err.msg))
    },
    getArticleCount(){
      getAllNoPage().then(res => {
        if (res.code===0){
          this.articleCount = res.data
        }else {
          this.$message.error('文章数量加载失败')
        }
      }).catch(err => this.$message.error(err.msg))
    },
    getUser () {
      getAllUser().then(res => {
        this.userCount = res.data.length
        this.userSex.rows[0]['总数'] = this.setSex(1, res.data)
        this.userSex.rows[1]['总数'] = this.setSex(0, res.data)
        this.setNewCustomer(res.data)
      }).catch(err => {
        console.log(err)
      })
    },
    setSex (sex, arr) {
      let count = 0
      for (let item of arr) {
        if (sex === item.sex) {
          count++
        }
      }
      return count
    },
    setNewCustomer(arr){
      for (let item of arr) {
          // 获得昨日新增用户数
          let createTime = new Date(item.createTime).setHours(0, 0, 0, 0);
          let today = new Date().setHours(0, 0, 0, 0);
          if ((createTime-today)===-86400000) this.newCustomer++

          // 获得用户地区分类数量
          for (let area of this.userArea.rows) {
            if (item.location.includes(area['地区'])) {
              area['总数']++
              break
            }
            if (area===this.userArea.rows[11]){
              this.userArea.rows[11]['总数']++
            }
          }
      }
    }

  }
}
</script>

<style>
.grid-content {
  display: flex;
  align-items: center;
  height: 100px;
}

.grid-cont-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #fcfbfb;
}

.grid-num {
  font-size: 30px;
  font-weight: bold;
}

.sep {
  margin-bottom: 50px;
}
</style>