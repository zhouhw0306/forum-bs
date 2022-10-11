<template>

  <el-card>
    <el-skeleton :rows="3" :loading="loading" animated/>
    <div slot="header" class="clearfix">
      <span>公告</span><img src="../../assets/img/laba.png" style="vertical-align: middle;width: 20px;margin-left: 3px" >
    </div>
    <ul class="me-category-list">
      <li v-for="n in notification" :key="n.id" class="me-category-item">
        <a @click="open(n.title,n.content)">{{n.title}}</a>
      </li>
    </ul>
  </el-card>

</template>

<script>
import {getAllNotify} from "@/api";

export default {
  name : 'CardMsg',
  data(){
    return{
      loading:true,
      notification:[]
    }
  },
  mounted() {
    this.init()
  },
  methods:{
    init(){
      getAllNotify().then(res => {
        if (res.code === 0){
          this.notification = res.data
          this.loading = false
        }else{
          this.$message({type: 'error', message: '积分榜加载失败', showClose: true})
        }
      })
    },
    open(title,content) {
      this.$alert(content, title, {
        confirmButtonText: '确定',
      });
    }
  }

}

</script>

<style>

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

</style>
