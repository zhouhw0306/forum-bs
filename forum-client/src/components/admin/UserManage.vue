<template>
  <div>
  <el-input v-model="select_word" size="mini" placeholder="筛选账号关键词" class="handle-input"></el-input>
  <el-table
      :data="tableData"
      style="width: 100%"
      height="80vh"
      :row-class-name="tableRowClassName">
    <el-table-column
        prop="id"
        label="id"
        width="180">
    </el-table-column>
    <el-table-column
        prop="username"
        label="账号">
    </el-table-column>
    <el-table-column label="性别">
      <template slot-scope="scope">
        <span v-if="scope.row.sex === 0">女<i class="el-icon-female" style="color: red"/></span>
        <span v-else>男<i class="el-icon-male" style="color: blue"/></span>
      </template>
    </el-table-column>
    <el-table-column
        prop="phoneNum"
        label="手机号">
    </el-table-column>
    <el-table-column
        prop="email"
        label="邮箱">
    </el-table-column>
    <el-table-column
        prop="birth"
        label="生日">
    </el-table-column>
    <el-table-column
        prop="score"
        label="积分">
    </el-table-column>
    <el-table-column
        prop="createTime"
        label="创建时间">
    </el-table-column>
    <el-table-column label="操作">
      <template slot-scope="scope">
        <el-button v-if="scope.row.lockState !== '0'"
            size="mini"
            type="danger"
            @click="handleLockDown(scope.row)">禁用</el-button>
        <el-button v-else
            size="mini"
            type="success"
            @click="handleLockDown(scope.row)">解禁</el-button>
      </template>
    </el-table-column>
  </el-table>
  </div>
</template>

<script>
import {getAllUser,lockOrUnlock} from "@/api";

export default {

  data() {
    return {
      tableData: [],
      tempData: [],
      select_word: ''
    }
  },
  watch: {
    select_word: function () {
      if (this.select_word === '') {
        this.tableData = this.tempData
      } else {
        this.tableData = []
        for (let item of this.tempData) {
          if (item.username.includes(this.select_word)) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    tableRowClassName({row, rowIndex}) {
      if (rowIndex%4 === 1) {
        return 'warning-row';
      } else if (rowIndex%4 === 3) {
        return 'success-row';
      }
      return '';
    },
    init(){
      getAllUser().then(res => {
        if (res.code===0){
          this.tableData = res.data
          this.tempData = res.data
        }else {
          this.$message.error(res.msg)
        }
      })
      .catch(err => this.$message.error(err.msg))
    },
    handleLockDown(row) {
      lockOrUnlock(row.id).then(res => {
        if (res.code===0){
          if (res.data === 0){
            row.lockState = '0'
            this.$message.warning('封禁成功')
          }else{
            row.lockState = '1'
            this.$message.success('解封成功')
          }
        }else {
          this.$message.error(res.msg)
        }
      }).catch(err => this.$message.error(err.msg))
    }
  }
}
</script>

<style>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}


</style>