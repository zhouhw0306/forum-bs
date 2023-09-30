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

/** 代码高亮stackoverflow-dark.css **/
pre code.hljs{
  display:block !important;
  overflow-x:auto !important;
  padding:20px !important;
  border-radius: 10px;
}
code.hljs{
  padding:3px 5px !important;
}
.hljs{
  color:#fff !important;
  background:#1c1b1b !important;
}
.hljs-subst{
  color:#fff !important;
}
.hljs-comment{
   color:#999 !important;
 }
.hljs-attr,.hljs-doctag,.hljs-keyword,.hljs-meta .hljs-keyword,.hljs-section,.hljs-selector-tag{
  color:#88aece !important;
}
.hljs-attribute{
  color:#c59bc1 !important;
}
.hljs-name,.hljs-number,.hljs-quote,.hljs-selector-id,.hljs-template-tag,.hljs-type{
  color:#f08d49 !important;
}
.hljs-selector-class{
  color:#88aece !important;
}
.hljs-link,.hljs-regexp,.hljs-selector-attr,.hljs-string,.hljs-symbol,.hljs-template-variable,.hljs-variable{
  color:#b5bd68 !important;
}
.hljs-meta,.hljs-selector-pseudo{
  color:#88aece !important;
}
.hljs-built_in,.hljs-literal,.hljs-title{
  color:#f08d49 !important;
}
.hljs-bullet,.hljs-code{
  color:#ccc !important;
}
.hljs-meta .hljs-string{
  color:#b5bd68 !important;
}
.hljs-deletion{
  color:#de7176 !important;
}
.hljs-addition{
  color:#76c490 !important;
}
.hljs-emphasis{
  font-style:italic !important;
}
.hljs-strong{
  font-weight:700 !important;
}
 pre::before{
  position: absolute;
  margin-top: 7px;
  margin-left: 20px;
  width: 12px;
  height: 12px;
  overflow: visible;
  font-weight: 700;
  font-size: 16px;
  line-height: 12px;
  white-space: nowrap;
  text-indent: 75px;
  background-color: #fc625d;
  border-radius: 16px;
  box-shadow: 20px 0 #fdbc40, 40px 0 #35cd4b;
  content: attr(codetype);
}
/** 代码高亮stackoverflow-dark.css **/
</style>