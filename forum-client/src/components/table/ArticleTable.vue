<template>
  <div class="search-articles"><el-empty v-if="!tableData.length" :image-size="80" description="没有找到相关讨论，试试其他关键词。" /><article v-for="item in tableData" :key="item.id" class="search-result-item"><router-link :to="'/view/' + item.id" class="search-result-title">{{ plainText(item.title) }}</router-link><p class="search-result-excerpt">{{ excerpt(item.content || item.contentHtml) }}</p><div class="search-result-meta"><span><i class="el-icon-chat-line-round"></i> {{ item.commentCount || 0 }} 条评论</span><span><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span><span>{{ (item.createTime || '').slice(0, 10) }}</span></div></article></div>
</template>
<script>
import { toPlainText, toExcerpt } from '@/utils/content'

export default {
  props:{
    tableData : {
      type: Array,
      default: () => []
    },
  },
  methods:{
    plainText: toPlainText,
    excerpt: toExcerpt,
    view(id){
      this.$router.push({path:`/view/${id}`})
    },
    filterHtml(strHTML){
      // let re = new RegExp('<(?!em).*?>','g');
      // strHTML = strHTML.replace(re ,"");
      strHTML = strHTML.replace(/<(?!em|\/em).*?>/g, "");
      return strHTML
    }
  }
}


</script>
<style scoped>

.search-result-item { padding: 20px; border-bottom: 1px solid var(--border-subtle); } .search-result-item:last-child { border-bottom: 0; }
.search-result-title { color: var(--accent); font-weight: 600; font-size: 16px; overflow-wrap: anywhere; }
.search-result-excerpt { color: var(--text-muted); font-size: 13px; line-height: 1.7; margin: 8px 0 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; overflow-wrap: anywhere; }
.search-result-meta { display: flex; gap: 16px; flex-wrap: wrap; color: var(--text-muted); font-size: 12px; }

</style>
