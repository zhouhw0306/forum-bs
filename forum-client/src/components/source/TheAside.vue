<template>
  <div class="sidebar">
    <el-menu
        class="sidebar-el-menu"
        :default-active="onRoutes"
        :collapse="collapse"
        background-color="white"
        text-color="black"
        active-text-color="#20a0ff"
        unique-opened
        router
    >
      <template v-for="item in items">
        <template>
          <el-menu-item :index="item.index" :key="item.index">
            <i :class="item.icon"></i>
            <span slot="title">{{ item.title }}</span>
          </el-menu-item>
        </template>
      </template>
    </el-menu>
  </div>
</template>

<script>
import bus from '@/assets/js/bus'

export default {
  data () {
    return {
      collapse: false,
      items: [
        {
          icon: 'el-icon-suitcase',
          index: 'tools',
          title: '工具箱'
        },
        {
          icon: 'el-icon-price-tag',
          index: 'sites',
          title: '实用网站'
        },
        {
          icon: 'el-icon-folder-opened',
          index: 'projects',
          title: '宝藏项目'
        },
        {
          icon: 'el-icon-video-camera',
          index: 'courses',
          title: '视频教程'
        }
      ]
    }
  },
  computed: {
    onRoutes () {
      return this.$route.path.replace('/', '')
    }
  },
  created () {
    // 通过 Event Bus 进行组件间通信，来折叠侧边栏
    bus.$on('collapse', msg => {
      this.collapse = msg
    })
  }
}
</script>

<style scoped>
.sidebar {
  display: block;
  /*position: absolute;*/
  background-color: #334256;
  left: 0;
  top: 60px;
  bottom: 0;
  overflow-y: scroll;
  position: fixed;
}
.sidebar::-webkit-scrollbar {
  width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
  width: 180px;
}
.sidebar > ul {
  height: 100%;
}
</style>