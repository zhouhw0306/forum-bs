<template>
  <div>
    <the-aside></the-aside>
    <div class="content-box" :class="{'content-collapse':collapse}" :style="{left:collapse?'70px':'160px'}">
      <div class="content">
        <transition name="move" mode="out-in">
          <keep-alive :include="tagsList">
            <router-view></router-view>
          </keep-alive>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import TheAside from '../components/admin/TheAside.vue'
import bus from '../assets/js/bus'

export default {
  components: {
    TheAside
  },
  data () {
    return {
      tagsList: [],
      collapse: false
    }
  },
  created () {
    bus.$on('collapse', msg => {
      this.collapse = msg
    })
    // 只有在标签页列表里的页面才使用keep-alive，即关闭标签之后就不缓存了。
    bus.$on('tags', msg => {
      let arr = []
      for (let i = 0, len = msg.length; i < len; i++) {
        msg[i].name && arr.push(msg[i].name)
      }
      this.tagsList = arr
    })
  }
}
</script>

<style>
.content-box {
  position: absolute;
  left: 150px;
  right: 0;
  top: 60px;
  bottom: 0;
  overflow-y: auto;
  -webkit-transition: left .3s ease-in-out;
  transition: left .3s ease-in-out;
}

.content {
  width: auto;
  padding: 20px;
}

.handle-input {
  width: 300px;
  margin-bottom: 10px;
  display: inline-block;
}
</style>
