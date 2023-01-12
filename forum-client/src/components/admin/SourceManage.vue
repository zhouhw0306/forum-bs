<template>
  <div>
  <el-radio v-model="state" label="1">已上线</el-radio>
  <el-radio v-model="state" label="0">待审核</el-radio>
  <el-input v-model="select_word" size="mini" placeholder="筛选标题关键词" class="handle-input"></el-input>
  <el-table
      :data="tableData"
      height="80vh"
      style="width: 100%">
    <el-table-column type="expand">
      <template slot-scope="props">
        <el-form label-position="left" inline class="demo-table-expand" style="margin-left: 20px">
          <el-form-item label="标题">
            <span>{{ props.row.title }}</span>
          </el-form-item>
          <el-form-item label="信息">
            <span>{{ props.row.description }}</span>
          </el-form-item>
          <el-form-item label="类型">
            <span>{{ props.row.category }}</span>
          </el-form-item>
          <el-form-item label="内容">
            <span>{{ props.row.content }}</span>
          </el-form-item>
          <el-form-item label="点赞数">
            <span>{{ props.row.thumbNum }}</span>
          </el-form-item>
          <el-form-item label="收藏数">
            <span>{{ props.row.favourNum }}</span>
          </el-form-item>
          <el-form-item label="userId">
            <span>{{ props.row.userId }}</span>
          </el-form-item>
          <el-form-item label="创建时间">
            <span>{{ props.row.createTime }}</span>
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
    <el-table-column
        label="资源 ID"
        prop="id">
    </el-table-column>
    <el-table-column
        label="标题"
        prop="title">
    </el-table-column>
    <el-table-column
        label="类型"
        prop="category">
    </el-table-column>
    <el-table-column label="操作">
<!--      <template slot-scope="props" v-if="props.row.state === 1">-->
<!--        -->
<!--      </template>-->
      <template slot-scope="props" >
        <el-button size="mini" v-if="props.row.state === 1"
                   type="danger"
                   @click="handleDelete(props.row.id)">删除</el-button>
        <el-button size="mini" v-if="props.row.state === 0"
                   type="success"
                   @click="handlePass(props.row.id,1)">通过</el-button>
        <el-button size="mini" v-if="props.row.state === 0"
                   type="danger"
                   @click="handlePass(props.row.id,0)">拒绝</el-button>
      </template>
    </el-table-column>
  </el-table>
  </div>
</template>

<script>
import {deleteSource, getSourceAll, sourcePass} from "@/api";

export default {
  data() {
    return {
      tableData: [],
      tempData: [],
      select_word: '',
      state: '1'
    }
  },
  watch: {
    select_word: function () {
      if (this.select_word === '') {
        this.tableData = this.tempData
      } else {
        this.tableData = []
        for (let item of this.tempData) {
          if (item.title.includes(this.select_word)) {
            this.tableData.push(item)
          }
        }
      }
    },
    state: function () {
      this.init()
    }

  },
  mounted() {
    this.init()
  },
  methods:{
    init(){
      getSourceAll(this.state).then(res => {
        if (res.code===0){
          this.tableData = res.data
          this.tempData = res.data
        }else {
          this.$message.error(res.msg)
        }
      }).catch(err => this.$message.error(err.msg))
    },
    // 删除操作
    handleDelete(id){
      deleteSource(id).then(res => {
        if (res.code === 0){
          this.init()
          this.$message.success('删除成功')
        }else {
          this.$message.error(res.msg)
        }
      }).catch(err => this.$message.error(err))
    },
    // 通过审核
    handlePass(id,type){
      let params = new URLSearchParams()
      params.append('id', id)
      params.append('type', type)
      sourcePass(params).then(res => {
        if (res.code === 0){
          this.init()
          if (res.data === 1){
            this.$message.success('上线成功')
          }
          if (res.data === 0){
            this.$message.warning('拒绝成功')
          }
        }else {
          this.$message.error(res.msg)
        }
      }).catch(err => this.$message.error(err))

    }
  }

}
</script>

<style>
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>