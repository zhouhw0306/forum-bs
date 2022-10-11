<template>

  <el-card>
    <el-skeleton :rows="9" :loading="loading" animated />
    <div slot="header" class="clearfix">
      <span style="font-size: x-large">积分榜</span><img src="../../assets/img/score.png" style="vertical-align: center;width: 20px;margin-left: 3px" >
    </div>
    <div v-for="u in users" :key="u.id" class="text item">
        <img class="me-list-picture" :src="attachImageUrl(u.avatar)"/>
        <span style="display: inline-block;width: 50%;margin-left: 10px">
          {{u.username}}
        </span>
        总积分: {{u.score}}
    </div>
  </el-card>

</template>


<script>
import {getByScore} from "@/api";
import {mixin} from "@/mixins";

export default {
  name:'CardList',
  mixins: [mixin],
  data(){
    return{
      users : [],
      loading : true
    }
  },
  mounted() {
    this.init()
  },
  methods:{
    init(){
      getByScore().then(res => {
        if (res.code===0) {
          this.users = res.data
          this.loading = false
        }else{
          this.$message({type: 'error', message: '积分榜加载失败', showClose: true})
        }
      })
    }
  }

}

</script>

<style>
.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
.me-list-picture {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  vertical-align: middle;
}
</style>