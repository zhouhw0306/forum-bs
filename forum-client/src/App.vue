<template>
  <div id="app">
    <router-view/>
    <go-top></go-top>
  </div>
</template>

<script>
import GoTop from '@/components/gotop/GoTop'
export default {
  name: 'App',
  components: { GoTop }
}
</script>

<style>

* {
  margin: 0;
  padding: 0;
}

body {
  min-height:100%;
  position:relative;
  background-image: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png");
  background-color: #f5f5f5;
  font-weight: 400;
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  line-height: 1.5;
}
a {
  cursor: pointer;
  text-decoration: none;
  transition: none 86ms ease-out;
}
a:hover {
  color: #409EFF;
}
.me-area{
  background-color: #fff;
  overflow: hidden;
}
.me-pull-right {
  float: right;
}
</style>