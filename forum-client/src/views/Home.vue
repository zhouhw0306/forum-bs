<template>
  <div id="home" style="height: 100%;background: url(https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png)">
    <el-container style="min-height: calc(100vh);">

      <the-header :activeIndex="activeIndex"></the-header>

      <router-view class="me-container" />

      <the-footer v-show="footerShow"></the-footer>

    </el-container>
  </div>
</template>

<script>
import TheHeader from "../components/TheHeader";
import TheFooter from "../components/TheFooter";
import {initBg} from "@/api";

export default {
  name:'Home',
  components : {TheHeader,TheFooter},
  data (){
    return {
      activeIndex: '/',
      footerShow:false
    }
  },
  beforeRouteEnter (to, from, next){
    next(vm => {
      vm.activeIndex = to.path
    })
  },
  beforeRouteUpdate (to, from, next) {
    if(to.path === '/'){
      this.footerShow = true
    }else{
      this.footerShow = false
    }
    next()
  }
}
</script>

<style>
body{
  background-color: whitesmoke;
}
.me-container{
  /*margin: 100px auto 140px;*/
  margin-top: 60px;
  min-height: calc(100vh - 60px);
}
</style>