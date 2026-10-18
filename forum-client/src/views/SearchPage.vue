<template>
  <div class="page-container search-page">
    <div class="page-heading"><div><div class="page-eyebrow"><i class="el-icon-discover"></i> CodeBase / 探索</div><h1>探索社区</h1><p>从一个关键词开始，找到你需要的答案。</p></div></div>
    <form class="search-form" role="search" @submit.prevent="searchByWord"><el-input aria-label="搜索关键词" v-model="inputValue" placeholder="搜索文章、资源或开发者" prefix-icon="el-icon-search" clearable /><el-button type="primary" native-type="submit">搜索</el-button></form>
    <div v-if="!searchedWord" class="search-intro state-panel"><i class="el-icon-search"></i><h3>你想了解些什么？</h3><p>输入技术名称、问题或开发者用户名，发现社区里的好内容。</p><div class="search-examples"><span>试试搜索</span><button v-for="word in ['Vue', 'Spring Boot', 'Docker']" :key="word" @click="inputValue = word; searchByWord()">{{ word }}</button></div></div>
    <section v-else class="search-results" aria-label="搜索结果">
      <div class="search-summary">“{{ searchedWord }}” 的搜索结果</div>
      <div v-if="searchError" class="state-panel" role="status"><i class="el-icon-warning-outline"></i><h3>搜索暂时不可用</h3><p>请稍后重试。</p><el-button size="small" @click="fetchSearch(searchedWord)">重新搜索</el-button></div>
      <el-skeleton v-else-if="loading" :rows="6" animated class="search-skeleton" />
      <el-tabs v-else v-model="activeName" @tab-click="handleClick"><el-tab-pane :label="'讨论 ' + articleData.length" name="articleTable"><article-table :tableData="articleData" /></el-tab-pane><el-tab-pane :label="'资源 ' + sourceData.length" name="sourceTable"><source-table :tableData="sourceData" /></el-tab-pane><el-tab-pane :label="'开发者 ' + userData.length" name="userTable"><user-table :tableData="userData" /></el-tab-pane></el-tabs>
    </section>
  </div>
</template>
<script>
import ArticleTable from "../components/table/ArticleTable";
import SourceTable from "../components/table/SourceTable";
import UserTable from "../components/table/UserTable";
import {searchData} from "@/api";

export default {
  data() {
    return {
      inputValue: '', // 搜索框
      loading: false,
      searchError: false,
      searchedWord: '',
      requestVersion: 0,
      activeName: 'articleTable',
      articleData:[],
      sourceData:[],
      userData:[]
    }
  },
  mounted() { this.syncSearch() },
  watch: { '$route.query': 'syncSearch' },
  beforeDestroy() { this.requestVersion += 1 },
  components:{ArticleTable,SourceTable,UserTable},
  methods: {
    handleClick() { this.$router.push({ query: { activeName: this.activeName, inputValue: this.searchedWord || undefined } }) },
    searchByWord() {
      const word = (this.inputValue || '').trim()
      if (word === (this.$route.query.inputValue || '')) { if (this.searchError) this.fetchSearch(word); return }
      this.$router.push({ query: { activeName: this.activeName, inputValue: word || undefined } })
    },
    syncSearch() {
      const validTabs = ['articleTable', 'sourceTable', 'userTable']
      this.activeName = validTabs.includes(this.$route.query.activeName) ? this.$route.query.activeName : 'articleTable'
      this.inputValue = this.$route.query.inputValue || ''
      if (this.inputValue !== this.searchedWord || this.searchError) this.fetchSearch(this.inputValue)
    },
    async fetchSearch(word) {
      const version = ++this.requestVersion
      this.searchedWord = word; this.searchError = false
      this.articleData = []; this.sourceData = []; this.userData = []
      if (!word) { this.loading = false; return }
      this.loading = true
      try {
        const result = await searchData(word)
        if (version !== this.requestVersion) return
        if (result.code !== 0 || !result.data) throw new Error('Search failed')
        this.articleData = result.data.articleData || []; this.sourceData = result.data.sourceData || []; this.userData = result.data.userData || []
      } catch (error) { if (version === this.requestVersion) this.searchError = true }
      finally { if (version === this.requestVersion) this.loading = false }
    }
  }
}
</script>
<style scoped>
.search-page { max-width: 1000px; }
.search-form { display: flex; gap: 10px; margin-bottom: 28px; } .search-form .el-input { flex: 1; min-width: 0; } .search-form .el-button { padding-left: 24px; padding-right: 24px; }
.search-intro { padding: 64px 24px; background: var(--surface); border: 1px solid var(--border); border-radius: 6px; }
.search-examples { display: flex; justify-content: center; align-items: center; flex-wrap: wrap; gap: 10px; font-size: 12px; } .search-examples button { background: var(--surface-muted); border: 1px solid var(--border); padding: 4px 10px; border-radius: 6px; cursor: pointer; color: var(--accent); } .search-examples button:hover { background: var(--hover); }
.search-results { border: 1px solid var(--border); border-radius: 6px; background: var(--surface); overflow: hidden; }
.search-summary { padding: 18px 20px 0; font-size: 13px; color: var(--text-muted); overflow-wrap: anywhere; }
.search-skeleton { padding: 24px; }
.search-results >>> .el-tabs__header { margin: 8px 0 0; } .search-results >>> .el-tabs__nav-wrap { padding: 0 20px; } .search-results >>> .el-tabs__nav-wrap::after { height: 1px; background: var(--border); }
.search-results >>> .el-tabs__content { padding: 0; }
</style>
