<template>
  <div class="article-manage management-panel">
    <div class="am-toolbar management-toolbar">
      <el-input v-model="keyword" size="small" placeholder="搜索标题..." prefix-icon="el-icon-search" clearable class="am-search management-search" />
      <span class="am-total management-count">{{ total }} 条讨论</span>
    </div>

    <div v-if="loadError" class="management-error" role="status"><i class="el-icon-warning-outline"></i> 讨论列表暂时无法加载 <button @click="loadData">重试</button></div>
    <el-table v-else v-loading="loading" :data="pagedData" style="width: 100%" size="medium">
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="userId" label="作者ID" width="120" show-overflow-tooltip />
      <el-table-column prop="viewCount" label="阅读" width="70" align="center" />
      <el-table-column prop="commentCount" label="评论" width="70" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" icon="el-icon-view" @click="preview(row)">预览</el-button>
          <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageSize"
      class="am-pager"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page.sync="currentPage"
    />

    <!-- 预览弹窗 -->
    <el-dialog :title="previewTitle" :visible.sync="dialogVisible" width="70%" top="5vh" destroy-on-close>
      <div class="am-preview-body"><markdown-editor :editor="{ value: previewContent, defaultOpen: 'preview' }" /></div>
    </el-dialog>
  </div>
</template>

<script>
import { deleteArticle, getArticles } from "@/api";
import MarkdownEditor from '@/components/article/MarkdownEditor'

export default {
  components: { MarkdownEditor },
  data() {
    return {
      tableData: [],
      allData: [],
      loading: false,
      loadError: false,
      keyword: "",
      currentPage: 1,
      pageSize: 15,
      dialogVisible: false,
      previewTitle: "",
      previewContent: "",
    };
  },
  computed: {
    filteredData() {
      if (!this.keyword) return this.allData;
      const kw = this.keyword.toLowerCase();
      return this.allData.filter(
        (row) =>
          (row.title || "").toLowerCase().includes(kw) ||
          (row.userId || "").toLowerCase().includes(kw)
      );
    },
    pagedData() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredData.slice(start, start + this.pageSize);
    },
    total() {
      return this.filteredData.length;
    },
  },
  watch: {
    keyword() { this.currentPage = 1; },
  },
  mounted() { this.loadData(); },
  methods: {
    async loadData() {
      this.loading = true; this.loadError = false
      try { const response = await getArticles(false, -1, { pageSize: 0, pageNumber: 0, sort: 'desc' }); if (response.data.code !== 0 || !Array.isArray(response.data.data)) throw new Error('Load failed'); this.allData = response.data.data; this.tableData = this.allData } catch (error) { this.loadError = true } finally { this.loading = false }
    },
    rowClass({ rowIndex }) {
      return rowIndex % 2 ? "am-even" : "";
    },
    preview(row) {
      this.previewTitle = row.title || "预览";
      this.previewContent = row.content || "";
      this.dialogVisible = true;
    },
    handleDelete(row) {
      this.$confirm("确认删除该文章？", "提示", { type: "warning" }).then(() => {
        deleteArticle(row.id).then((res) => {
          if (res.code === 0) {
            this.$message.success("已删除");
            this.allData = this.allData.filter((r) => r.id !== row.id);
          } else {
            this.$message.error("删除失败");
          }
        }).catch(() => this.$message.error("删除失败"));
      }).catch(() => {});
    },
  },
};
</script>

<style scoped>
.article-manage { padding: 0; }
.am-toolbar {
  display: flex; align-items: center; gap: 16px;
  margin-bottom: 16px;
}
.am-search { width: 280px; }
.am-total { font-size: 13px; color: #909399; }

.am-pager { margin-top: 20px; text-align: center; }

.am-preview-body {
  max-height: 70vh;
  overflow-y: auto;
  padding: 16px 20px;
  background: var(--surface);
  border-radius: 8px;
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}
</style>

<style>
.am-even { background: #fafbfc; }
</style>
