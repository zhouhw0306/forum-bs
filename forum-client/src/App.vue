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
html, body { margin: 0; padding: 0; min-height: 100%; width: 100%; }
* { margin: 0; padding: 0; }
.comment-content pre code.hljs { display: block; overflow-x: auto; padding: 16px; border-radius: 6px; color: #d4d4d4; background: #1e1e1e; }
.comment-content code.hljs { padding: 3px 5px; border-radius: 4px; color: #cf222e; background: #f6f8fa; }
</style>
