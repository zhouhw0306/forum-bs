<template>
  <div class="sidebar">
    <el-menu
        class="sidebar-el-menu"
        :default-active="onRoutes"
        :collapse="collapse"
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
    <div class="aside-control-button" @click="collapseChange">
      <i v-if="collapse" class="el-icon-caret-right"></i>
      <i v-else class="el-icon-caret-left"></i>
    </div>
  </div>
</template>

<script>
import bus from '../../assets/js/bus'

export default {
  data () {
    return {
      collapse: false,
      items: [
        {
          icon: 'el-icon-user',
          index: 'userManage',
          title: '用户管理'
        },
        {
          icon: 'el-icon-tickets',
          index: 'articleManage',
          title: '帖子管理'
        },
        {
          icon: 'el-icon-chat-line-round',
          index: 'commentManage',
          title: '评论管理'
        },
        {
          icon: 'el-icon-copy-document',
          index: 'sourceManage',
          title: '资源管理'
        }
      ]
    }
  },
  computed: {
    onRoutes () {
      return this.$route.path.replace('/', '')
    }
  },
  methods: {
    collapseChange () {
      this.collapse = !this.collapse
      bus.$emit('collapse', this.collapse)
    }
  }
}
</script>

<style scoped>
.sidebar {
  display: block;
  position: absolute;
  background-color: #334256;
  left: 0;
  top: 60px;
  bottom: 0;
}
.sidebar::-webkit-scrollbar {
  width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
  width: 150px;
}
.sidebar > ul {
  height: 100%;
}
.aside-control-button{
  position: absolute;
  width: 20px;
  height: 80px;
  background-color: rgba(232, 232, 232, 0.53);
  right: -20px;
  top:35%;
  transform:perspective(2em) rotateY(20deg);
  text-align: center;
  line-height: 80px;
  color: #909399;
  z-index: 1145;
}
.aside-control-button:hover{
  background-color: #ecf5ff;
}
</style>