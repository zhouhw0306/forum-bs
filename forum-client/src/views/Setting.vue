<template>
  <div class="setting-page">
    <div class="setting-container">
      <h2 class="setting-title">账号设置</h2>
      <!-- Tab 切换 -->
      <div class="setting-tabs">
        <button
          v-for="item in settingList" :key="item.path"
          :class="['setting-tab', { active: activeName === item.name }]"
          @click="handleClick(item)"
        >
          <i :class="item.icon"></i> {{ item.name }}
        </button>
      </div>
      <!-- 内容区 -->
      <div class="setting-body">
        <component :is="componentSrc" />
      </div>
    </div>
  </div>
</template>

<script>
import Info from "../components/Info";
import Upload from "../components/Upload";

export default {
  name: "Setting",
  components: { Info, Upload },
  data() {
    return {
      activeName: "个人信息",
      componentSrc: "Info",
      settingList: [
        { icon: "el-icon-user", name: "个人信息", path: "Info" },
        { icon: "el-icon-picture-outline", name: "修改头像", path: "Upload" },
      ],
    };
  },
  methods: {
    handleClick(item) {
      this.activeName = item.name;
      this.componentSrc = item.path;
    },
  },
};
</script>

<style scoped>
.setting-page {
  min-height: calc(100vh - 60px);
  background: transparent;
}
.setting-container {
  max-width: 700px;
  width: 92%;
  margin: 0 auto;
  padding: 28px 0 60px;
}
.setting-title {
  margin: 0 0 20px;
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

/* Tab */
.setting-tabs {
  display: flex;
  gap: 6px;
  background: #fff;
  border-radius: 14px;
  padding: 5px;
  margin-bottom: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);
}
.setting-tab {
  flex: 1;
  padding: 12px 0;
  border: none;
  border-radius: 10px;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  cursor: pointer;
  transition: all .2s;
}
.setting-tab:hover { color: #409eff; background: #f0f5ff; }
.setting-tab.active {
  color: #fff;
  background: linear-gradient(135deg, #409eff, #337ecc);
  box-shadow: 0 2px 8px rgba(64,158,255,.3);
}
.setting-tab i { margin-right: 4px; }

/* 内容卡 */
.setting-body {
  background: #fff;
  border-radius: 16px;
  padding: 28px 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);
  min-height: 400px;
}
</style>
