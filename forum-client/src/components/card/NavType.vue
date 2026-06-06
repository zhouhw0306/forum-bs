<template>
  <el-menu
    :default-active="activeNav"
    class="el-menu-vertical-demo"
    @select="handleSelect"
  >
    <el-menu-item index="care">
      <i class="el-icon-menu"></i>
      <span slot="title">关注</span>
    </el-menu-item>
    <el-menu-item index="">
      <i class="el-icon-s-promotion"></i>
      <span slot="title">综合</span>
    </el-menu-item>
    <el-menu-item
      v-for="cat in categories"
      :key="cat.id"
      :index="String(cat.id)"
    >
      <i :class="catIcon(cat.id)"></i>
      <span slot="title">{{ cat.categoryName }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
import bus from "../../assets/js/bus";
import { getAllCategorys } from "@/api";

const iconList = [
  "el-icon-s-platform", "el-icon-s-finance", "el-icon-info",
  "el-icon-question", "el-icon-s-check", "el-icon-s-claim",
  "el-icon-s-cooperation", "el-icon-s-order", "el-icon-s-tools",
  "el-icon-s-data"
];

export default {
  data() {
    return {
      activeNav: "",
      categories: [],
    };
  },
  mounted() {
    bus.$on("activeNav", (index) => {
      this.activeNav = index;
    });
    this.fetchCategories();
  },
  methods: {
    async fetchCategories() {
      try {
        const res = await getAllCategorys();
        if (res.code === 0) this.categories = res.data || [];
      } catch (e) {
        console.error("加载分类失败", e);
      }
    },
    catIcon(id) {
      return iconList[(id || 0) % iconList.length];
    },
    handleSelect(index) {
      if (index === "care") {
        this.$router.push({ query: { sw: index } });
      } else if (index) {
        this.$router.push({ query: { sw: index } });
      } else {
        this.$router.push({ query: {} });
      }
      bus.$emit("switchType", index);
    },
  },
  beforeDestroy() {
    bus.$off("activeNav");
  },
};
</script>

<style scoped>
.el-menu-vertical-demo {
  padding: 10px;
  height: auto;
  min-height: 300px;
}
.el-menu-item {
  width: 150px;
  height: 50px;
}
.el-menu-item:hover {
  background-color: rgb(247, 248, 250);
}
.is-active {
  border-left: #30a4fc solid 3px;
  background-color: rgb(236, 245, 255);
}
</style>
