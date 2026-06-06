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

html, body {
  margin: 0 !important;
  padding: 0 !important;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

* {
  margin: 0;
  padding: 0;
}

body {
  min-height:100%;
  position:relative;
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

/* 评论区代码高亮（文章正文由 ByteMD 高亮插件负责）*/
.comment-content pre code.hljs {
  display: block !important;
  overflow-x: auto !important;
  padding: 20px !important;
  border-radius: 10px;
  color: #d4d4d4 !important;
  background: #1e1e1e !important;
}
.comment-content code.hljs {
  padding: 3px 5px !important;
  color: #e74c3c !important;
  background: #f0f0f0 !important;
}
</style>
