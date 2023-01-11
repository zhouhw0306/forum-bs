<template>
  <div>
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
  </el-table>
  </div>
</template>

<script>
import {getSourceAll} from "@/api";

export default {
  data() {
    return {
      tableData: [],
      tempData: [],
      select_word:''
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
    }
  },
  mounted() {
    this.init()
  },
  methods:{
    init(){
      getSourceAll().then(res => {
        if (res.code===0){
          this.tableData = res.data
          this.tempData = res.data
        }else {
          this.$message.error(res.msg)
        }
      }).catch(err => this.$message.error(err.msg))
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