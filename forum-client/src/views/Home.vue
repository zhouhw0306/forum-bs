<template>
  <div id="home">
    <el-container>

      <the-header :activeIndex="activeIndex"></the-header>

      <router-view class="me-container"/>

    </el-container>
  </div>
</template>

<script>
import TheHeader from "../components/TheHeader";

export default {
  name: 'Home',
  components: {TheHeader},
  data() {
    return {
      activeIndex: '/'
    }
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.activeIndex = to.path
    })
  }
}
</script>

<style>
body{
  background-color: whitesmoke;
}
.me-container{
  width: 100%;
  margin-top: 60px;
  min-height: calc(100vh - 60px);
}
</style>