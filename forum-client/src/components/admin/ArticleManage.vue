<template>
  <div>
  <el-input v-model="select_word" size="mini" placeholder="筛选标题关键词" class="handle-input"></el-input>
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
        prop="userId"
        label="作者Id"
        width="180">
    </el-table-column>
    <el-table-column
        prop="title"
        label="标题"
        width="150">
    </el-table-column>
    <el-table-column label="正文">
      <template slot-scope="scope">
        <el-button size="mini" @click="viewArticle(scope.row)">预览</el-button>
      </template>
    </el-table-column>
    <el-table-column
        prop="createTime"
        label="创建时间">
    </el-table-column>
    <el-table-column
        prop="updateTime"
        label="创建时间">
    </el-table-column>
    <el-table-column label="操作">
      <template slot-scope="scope">
        <el-button size="mini"
                   type="danger"
                   @click="handleDelete(scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog :title="'标题：' + currentArticle.title" :visible.sync="dialogTableVisible">
      <markdown-editor style="box-shadow: none;height: 50vh" :editor=editor></markdown-editor>
  </el-dialog>
  </div>
</template>

<script>
import MarkdownEditor from '@/components/article/MarkdownEditor'
import {deleteArticle, getArticles} from "@/api";

export default {

  data() {
    return {
      tableData: [],
      tempData: [],
      select_word: '',
      innerPage: {
        pageSize: 0,
        pageNumber: 0,
        sort: 'desc'
      },
      currentArticle: {title:'',content:''},
      dialogTableVisible : false,
      editor: {
        value: '',
        toolbarsFlag: false,
        subfield: false,
        defaultOpen: 'preview'
      }
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
      getArticles(false,-1,this.innerPage).then(res => {
        if (res.data.code===0){
          this.tableData = res.data.data
          this.tempData = res.data.data
        }else {
          this.$message.error(res.data.msg)
        }
      }).catch(err => this.$message.error(err.data.msg))
    },
    // 删除文章
    handleDelete(row){
        deleteArticle(row.id).then(res => {
          if (res.code === 0){
            this.$message.success('删除成功')
            this.init()
          }else{
            this.$message.error('删除失败')
          }
        }).catch(err => {
          this.$message.error(err.msg)
        })
    },
    viewArticle(article){
      this.dialogTableVisible = true
      this.currentArticle = article
      this.editor.value = article.content
    }
  },
  components: {MarkdownEditor}
}
</script>

<style>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
pre::before {
  display: none;
}
.markdown-body .highlight pre, .markdown-body pre{
  padding: 0 !important;
}
.hljs{
  padding: 20px;
  border-radius: 10px;
}
</style>