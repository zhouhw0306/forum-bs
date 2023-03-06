<template>
  <div style="margin: 150px auto;width: 60vw;min-width: 900px">
    <div>
      <el-input placeholder="请输入内容" v-model="inputValue" class="input-with-select" @keyup.enter.native="searchByWord">
        <el-button @click="searchByWord" slot="append" icon="el-icon-search"></el-button>
      </el-input>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="帖子" name="articleTable"><ArticleTable :tableData="articleData"></ArticleTable></el-tab-pane>
      <el-tab-pane label="资源" name="sourceTable"><SourceTable :tableData="sourceData"></SourceTable></el-tab-pane>
      <el-tab-pane label="用户" name="userTable"><UserTable :tableData="userData"></UserTable></el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import ArticleTable from "../components/table/ArticleTable";
import SourceTable from "../components/table/SourceTable";
import UserTable from "../components/table/UserTable";
import {searchData} from "@/api";

export default {
  data() {
    return {
      inputValue: '', // 搜索框
      activeName: 'articleTable',
      articleData:[],
      sourceData:[],
      userData:[]
    }
  },
  mounted() {
    if (this.$route.query.activeName){
      this.activeName = this.$route.query.activeName
    }
    this.inputValue = this.$route.query.inputValue
    this.searchByWord()
  },
  components:{ArticleTable,SourceTable,UserTable},
  methods:{
    handleClick(tab, event) {
      this.$router.push({query:{activeName:this.activeName,inputValue:this.inputValue}})
    },
    searchByWord(){
      this.$router.push({query:{activeName:this.activeName,inputValue:this.inputValue}})
      searchData(this.inputValue).then(res => {
        this.articleData = res.data.articleData
        this.sourceData = res.data.sourceData
        this.userData = res.data.userData
      })
    }
  }
}
</script>


<style>
.el-tabs__header{
  background-color: white;
  margin: 15px 0;
}
.el-tabs__nav-wrap{
  padding: 0px 20px 0px 20px;

}
</style>