<template>
  <div><el-empty v-if="!tableData.length" :image-size="80" description="没有找到相关资源，试试其他关键词。" /><div v-else class="search-resource-grid"><article v-for="item in tableData" :key="item.id" class="search-resource"><i class="el-icon-folder-opened"></i><router-link :to="'/details/' + item.id">{{ plainText(item.title) }}</router-link><p>{{ item.description }}</p><router-link :to="'/details/' + item.id" class="search-resource-open">查看资源 <i class="el-icon-right"></i></router-link></article></div></div>
</template>
<script>
import {mixin} from "@/mixins";
import { toPlainText } from '@/utils/content'

export default {
  mixins: [mixin],
  props:{
    tableData : {
      type: Array,
      default: () => []
    },
  },
  methods: {
    plainText: toPlainText,
    arr(src) {
      let arr = []
      arr.push(src)
      return arr
    },
    view(id){
      this.$router.push({path: `/details/${id}`})
    }
  }
}


</script>
<style scoped>

.search-resource-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; padding: 20px; }
.search-resource { border: 1px solid var(--border); border-radius: 6px; padding: 20px; min-width: 0; display: flex; flex-direction: column; align-items: flex-start; gap: 12px; } .search-resource > i { color: var(--text-muted); font-size: 22px; } .search-resource > a { font-weight: 600; overflow-wrap: anywhere; }
.search-resource p { color: var(--text-muted); font-size: 13px; line-height: 1.7; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; overflow-wrap: anywhere; }
.search-resource .search-resource-open { font-size: 12px; font-weight: 400; margin-top: auto; color: var(--text-muted); }
@media (max-width: 600px) { .search-resource-grid { grid-template-columns: 1fr; padding: 16px; } }

</style>
