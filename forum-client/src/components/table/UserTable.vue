<template>
  <div><el-empty v-if="!tableData.length" :image-size="80" description="没有找到相关开发者，试试其他关键词。" /><div v-else class="search-users"><article v-for="item in tableData" :key="item.id" class="search-user"><img :src="attachImageUrl(item.avatar)" :alt="item.username" @error="e => e.target.src = avatarFallback(item.username)" /><div><h3>{{ item.username }}</h3><p>{{ item.introduction || '这位开发者还没有填写简介。' }}</p></div></article></div></div>
</template>
<script>
import {mixin} from "@/mixins";

export default {
  mixins: [mixin],
  props:{
    tableData : {
      type: Array,
      default: () => []
    },
  }
}


</script>
<style scoped>

.search-user { display: flex; gap: 16px; padding: 24px 20px; border-bottom: 1px solid var(--border-subtle); } .search-user:last-child { border-bottom: 0; }
.search-user img { width: 44px; height: 44px; border-radius: 50%; object-fit: cover; border: 1px solid var(--border); flex-shrink: 0; }
.search-user > div { min-width: 0; } .search-user h3 { font-size: 15px; font-weight: 600; margin-bottom: 6px; } .search-user p { color: var(--text-muted); font-size: 13px; overflow-wrap: anywhere; }

</style>
