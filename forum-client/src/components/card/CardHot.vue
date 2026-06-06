<template>

  <el-card>
    <el-skeleton :rows="6" :loading="loading" animated />
    <div slot="header" class="clearfix">
      <span>热帖</span><img src="../../assets/img/hot.png" style="vertical-align: middle;width: 20px;margin-left: 3px" >
    </div>
    <ul class="me-category-list">
      <li v-for="a in articles" :key="a.id" class="me-category-item">
        <a @click="view(a.id)" class="title-hot">{{a.title}}</a>
        <span style="float: right">
           {{a.createTime.substring(0,10)}}
        </span>
      </li>
    </ul>
  </el-card>

</template>

<script>
import {getHot} from "@/api";

export default {
  name: 'CardHot',
  data(){
    return{
      articles:[],
      loading : true //骨架屏
    }
  },
  mounted() {
    this.init()
  },
  methods:{
    view(id) {
      this.$router.push({path: `/view/${id}`})
    },
    init(){
      getHot().then(res => {
        if (res.code===0){
          this.articles = res.data
          this.loading = false
        }else{
          this.$message({type: 'error', message: '热帖加载失败', showClose: true})
        }
      })
    }
  }

}

</script>

<style>
.me-category-list {
  list-style-type: none;
}

.me-category-item {
  padding: 4px;
  font-size: 14px;
  margin-bottom: 10px;
}

.me-category-item a:hover {
  text-decoration: underline;
}

.clearfix:before,

.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.title-hot{
  white-space: nowrap;
  width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
}
</style>