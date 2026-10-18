<template>
  <section class="management-panel resource-management">
    <header class="management-toolbar"><div class="resource-review-tabs" role="tablist" aria-label="资源审核状态"><button v-for="tab in states" :key="tab.value" role="tab" :aria-selected="String(state === tab.value)" :class="{ active: state === tab.value }" @click="state = tab.value"><i :class="tab.icon"></i>{{ tab.label }}</button></div><span class="management-count">{{ filteredData.length }} 个资源</span><el-input v-model="keyword" size="small" placeholder="搜索资源标题…" prefix-icon="el-icon-search" clearable class="management-search resource-management-search" /></header>
    <div v-if="loadError" class="management-error" role="status"><i class="el-icon-warning-outline"></i> 资源列表暂时无法加载 <button @click="loadData">重试</button></div>
    <el-table v-else v-loading="loading" :data="pagedData" style="width: 100%" empty-text="当前状态下暂无资源">
      <el-table-column type="expand" width="42"><template slot-scope="{ row }"><dl class="resource-expand"><div><dt>资源描述</dt><dd>{{ row.description || '未填写' }}</dd></div><div><dt>资源外链</dt><dd>{{ row.content || '未填写' }}</dd></div><div><dt>作者 ID</dt><dd>{{ row.userId }}</dd></div><div><dt>资源附件</dt><dd><el-link v-if="row.fileName" :href="row.fileUrl" type="primary">{{ row.fileName }}</el-link><span v-else>无附件</span></dd></div></dl></template></el-table-column>
      <el-table-column label="资源标题" min-width="200" show-overflow-tooltip><template slot-scope="{ row }"><span class="managed-resource-title">{{ row.title }}</span><span class="managed-resource-id">#{{ row.id }}</span></template></el-table-column>
      <el-table-column label="类型" width="90"><template slot-scope="{ row }"><el-tag type="info" size="mini">{{ categoryName(row.category) }}</el-tag></template></el-table-column>
      <el-table-column prop="thumbNum" label="点赞" width="70" align="center" /><el-table-column prop="favourNum" label="收藏" width="70" align="center" /><el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="160" align="right"><template slot-scope="{ row }"><el-button v-if="Number(row.state) === 0" type="text" size="mini" @click="review(row, 1)">通过</el-button><el-button v-if="Number(row.state) === 0" type="text" size="mini" class="review-reject" @click="review(row, -1)">驳回</el-button><el-button v-if="Number(row.state) === 1" type="text" size="mini" class="review-reject" @click="remove(row)">删除</el-button></template></el-table-column>
    </el-table>
    <el-pagination v-if="filteredData.length > pageSize" background layout="prev, pager, next" :pager-count="5" :total="filteredData.length" :page-size="pageSize" :current-page.sync="page" />
  </section>
</template>
<script>
import { deleteSource, getSourceAll, sourcePass } from '@/api'
export default {
  data: () => ({ records: [], keyword: '', state: '1', loading: false, loadError: false, page: 1, pageSize: 12, requestVersion: 0, states: [{ value: '1', label: '已上线', icon: 'el-icon-circle-check' }, { value: '0', label: '待审核', icon: 'el-icon-time' }, { value: '-1', label: '已驳回', icon: 'el-icon-circle-close' }] }),
  computed: { filteredData() { const keyword = this.keyword.trim().toLowerCase(); return this.records.filter(row => (row.title || '').toLowerCase().includes(keyword)) }, pagedData() { return this.filteredData.slice((this.page - 1) * this.pageSize, this.page * this.pageSize) } },
  watch: { state() { this.page = 1; this.loadData() }, keyword() { this.page = 1 } },
  mounted() { this.loadData() },
  beforeDestroy() { this.requestVersion += 1 },
  methods: {
    categoryName(value) { return { 1: '工具', 2: '网站', 3: '项目', 4: '教程' }[value] || value || '未分类' },
    async loadData() {
      const version = ++this.requestVersion; this.loading = true; this.loadError = false
      try { const result = await getSourceAll(this.state); if (version !== this.requestVersion) return; if (result.code !== 0 || !Array.isArray(result.data)) throw new Error('Load failed'); this.records = result.data } catch (error) { if (version === this.requestVersion) this.loadError = true } finally { if (version === this.requestVersion) this.loading = false }
    },
    async remove(row) {
      try { await this.$confirm('删除资源「' + row.title + '」？', '删除资源', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }) } catch (error) { return }
      try { const result = await deleteSource(row.id); if (result.code !== 0) throw new Error(result.msg); this.$message.success('资源已删除'); this.page = 1; this.loadData() } catch (error) { this.$message.error('删除失败，请稍后重试') }
    },
    async review(row, type) {
      const params = new URLSearchParams(); params.append('id', row.id); params.append('type', type)
      try { const result = await sourcePass(params); if (result.code !== 0) throw new Error(result.msg); this.$message.success(type === 1 ? '资源已上线' : '资源已驳回'); this.page = 1; this.loadData() } catch (error) { this.$message.error('审核失败，请稍后重试') }
    }
  }
}
</script>
<style scoped>
.resource-management-search { margin-left: auto; width: 220px; } .managed-resource-title { display: block; font-size: 12px; font-weight: 500; } .managed-resource-id { display: block; font-size: 10px; color: var(--text-muted); margin-top: 3px; } .review-reject { color: var(--danger) !important; }
</style>
