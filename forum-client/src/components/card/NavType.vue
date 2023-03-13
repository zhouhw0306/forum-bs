<template>
      <el-menu
          :default-active="activeNav"
          class="el-menu-vertical-demo"
          @select="handleSelect">
        <el-menu-item index="care">
          <i class="el-icon-menu"></i>
          <span slot="title">关注</span>
        </el-menu-item>
        <el-menu-item index="">
          <i class="el-icon-s-promotion"></i>
          <span slot="title">综合</span>
        </el-menu-item>
        <el-menu-item index="1">
          <i class="el-icon-s-platform"></i>
          <span slot="title">后端</span>
        </el-menu-item>
        <el-menu-item index="2">
          <i class="el-icon-s-finance"></i>
          <span slot="title">前端</span>
        </el-menu-item>
        <el-menu-item index="3">
          <i class="el-icon-info"></i>
          <span slot="title">Android</span>
        </el-menu-item>
        <el-menu-item index="4">
          <i class="el-icon-question"></i>
          <span slot="title">iOS</span>
        </el-menu-item>
        <el-menu-item index="5">
          <i class="el-icon-s-check"></i>
          <span slot="title">人工智能</span>
        </el-menu-item>
        <el-menu-item index="6">
          <i class="el-icon-s-claim"></i>
          <span slot="title">数据结构</span>
        </el-menu-item>
        <el-menu-item index="7">
          <i class="el-icon-s-cooperation"></i>
          <span slot="title">开发工具</span>
        </el-menu-item>
      </el-menu>
</template>

<script>
import bus from '../../assets/js/bus'

export default {
  data(){
    return{
      activeNav: ''
    }
  },
  mounted() {
    bus.$on('activeNav',(index)=>{
      this.activeNav = index
    })
  },
  methods: {
    handleSelect(index){
      if (index){
        this.$router.push({query:{sw:index}})
      }else {
        this.$router.push({query:{}})
      }
      bus.$emit('switchType',index)
    }
  },
  beforeDestroy() {
    bus.$off('activeNav')
  }
}
</script>

<style scoped>
.el-menu-vertical-demo{
  padding: 10px;
  height: 450px;
}
.el-menu-item{
  width: 150px;
  height: 50px;
  /*border-radius: 5px;*/
}
.el-menu-item:hover{
  background-color: rgb(247,248,250);
}
.is-active{
  border-left: #30a4fc solid 3px;
}
</style>