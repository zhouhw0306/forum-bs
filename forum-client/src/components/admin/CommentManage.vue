<template>
  <div>
    <el-input v-model="select_word" size="mini" placeholder="筛选账号关键词" class="handle-input"></el-input>
    <el-table
        :data="tableData"
        style="width: 100%"
        height="80vh"
        row-key="id"
        border
        lazy
        :row-class-name="tableRowClassName"
        :tree-props="{children: 'childrens', hasChildren: children!=null}">
      <el-table-column
          prop="id"
          label="id"
          width="180">
      </el-table-column>
      <el-table-column
          prop="user.username"
          label="评论人"
          width="180">
      </el-table-column>
      <el-table-column
          prop="content"
          label="内容">
      </el-table-column>
      <el-table-column
          prop="articleId"
          label="所属帖子">
      </el-table-column>
      <el-table-column
          prop="createTime"
          label="创建时间">
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import {getCommentAll} from "@/api";

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
          if (item.user.username.includes(this.select_word)) {
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
    init(){
      getCommentAll().then(res => {
        if (res.code===0){
          this.tableData = res.data
          this.tempData = res.data
        }else {
          this.$message.error(res.msg)
        }
      }).catch(err => this.$message.error(err.msg))
    },
    tableRowClassName({row, rowIndex}) {
      if (row.level === '0') {
        return ''
      } else if (row.level === '1') {
        return 'row-1'
      } else{
        return 'row-2';
      }
    },
  },
}
</script>

<style>
.el-table .row-0 {
  background: #fac9c9;
}
.el-table .row-1 {
  background: #d6fad3;
}
.el-table .row-2 {
  background: #f6f2d0;
}
</style>