<template>
  <div id="home">
    <el-container>

      <the-header :activeIndex="activeIndex"></the-header>

      <router-view class="me-container"/>

      <the-footer v-show="footerShow"></the-footer>

    </el-container>
  </div>
</template>

<script>
import TheHeader from "../components/TheHeader";
import TheFooter from "../components/TheFooter";

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
    this.footerShow = to.path === '/';
    next()
  }
}
</script>

<style>
body{
  background-color: whitesmoke;
}
.me-container{
  margin-top: 60px;
  min-height: calc(100vh - 60px);
}
</style>